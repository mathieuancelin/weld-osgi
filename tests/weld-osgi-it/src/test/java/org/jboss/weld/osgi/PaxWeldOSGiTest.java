package org.jboss.weld.osgi;

import org.jboss.weld.environment.osgi.api.extension.Registration;
import com.sample.osgi.api.DummyService;
import com.sample.osgi.api.ServiceBundle1;
import java.util.Properties;
import org.junit.Assert;
import org.osgi.framework.ServiceReference;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import static org.ops4j.pax.exam.LibraryOptions.*;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@RunWith(JUnit4TestRunner.class)
public class PaxWeldOSGiTest {

    @Configuration
    public static Option[] configure() {
        return options(
            mavenBundle("org.jboss.weld.osgi", "weld-osgi").version("1.0-SNAPSHOT"),
            mavenBundle("org.jboss.weld.osgi", "weld-osgi-integration-bundle-api").version("1.0-SNAPSHOT"),
            mavenBundle("org.jboss.weld.osgi", "weld-osgi-integration-bundle1").version("1.0-SNAPSHOT"),
            mavenBundle("org.jboss.weld.osgi", "weld-osgi-integration-bundle2").version("1.0-SNAPSHOT"),
            junitBundles(),
            felix()
        );
    }

    @Test
    public void testBasicServicesRegistration(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference[] instances = context.getServiceReferences(Instance.class.getName(), null);
        ServiceReference[] events = context.getServiceReferences(Event.class.getName(), null);
        ServiceReference[] beanmanagers = context.getServiceReferences(BeanManager.class.getName(), null);
        Assert.assertEquals(instances.length, 3);
        Assert.assertEquals(events.length, 3);
        Assert.assertEquals(beanmanagers.length, 3);
    }

    @Test
    public void testSpecificServicesRegistration(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle1.class.getName());
        if (ref != null) {
            ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref);
            if (service1 != null) {
                Assert.assertEquals(service1.getStart(), 1);
                Assert.assertTrue(service1.getArrival() > 1);
                int oldArrival = service1.getArrival();
                int oldDeparture = service1.getDeparture();
                int oldInvalid = service1.getInvalid();
                DummyService dummy = service1.getDummy();
                Registration<DummyService> reg = service1.getRegistry().registerService(DummyService.class, dummy);
                Assert.assertEquals(service1.getValid(), 1);
                Assert.assertEquals(service1.getDummyArrival(), 1);
                Assert.assertEquals(context.getServiceReferences(DummyService.class.getName(), null).length, 1);
                Assert.assertEquals(service1.getArrival(), oldArrival + 1);
                Assert.assertEquals(service1.getRegistry().getServiceReferences(DummyService.class).size(), 1);
                reg.unregister();
                Assert.assertEquals(service1.getInvalid(), oldInvalid + 1);
                Assert.assertNull(context.getServiceReferences(DummyService.class.getName(), null));
                Assert.assertEquals(service1.getDummyDeparture(), 1);
                Assert.assertEquals(service1.getDeparture(), oldDeparture + 1);
                Assert.assertEquals(service1.getRegistry().getServiceReferences(DummyService.class).size(), 0);
                Assert.assertEquals(service1.getStop(), 0);
            } else {
                Assert.fail("service is null");
            }
        } else {
            Assert.fail("service ref is null");
        }
    }

    @Test
    public void testOSGiInjection(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle1.class.getName());
        if (ref != null) {
            ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref);
            if (service1 != null) {
                DummyService dummy = service1.getDummy();
                Registration<DummyService> reg = service1.getRegistry().registerService(DummyService.class, dummy);
                Assert.assertEquals(service1.timesCollection(3, 3), 9);
                Assert.assertEquals(service1.timesOSGi(3, 3), 9);
                Assert.assertEquals(service1.timesProvider(3, 3), 9);
                reg.unregister();
            } else {
                Assert.fail("service is null");
            }
        } else {
            Assert.fail("service ref is null");
        }
    }

//    @Test
//    public void testFilteredOSGiInjection(BundleContext context) throws Exception {
//        globalWait(context);
//        ServiceReference ref = context.getServiceReference(ServiceBundle1.class.getName());
//        if (ref != null) {
//            ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref);
//            if (service1 != null) {
//                Properties p = new Properties();
//                p.setProperty("value", "fake");
//                context.registerService(DummyService.class.getName(), new FakeDummy(), p);
//                Assert.assertEquals(service1.filteredtimesCollection(3, 3), 18);
//                Assert.assertEquals(service1.filteredtimesOSGi(3, 3), 18);
//                Assert.assertEquals(service1.filteredtimesProvider(3, 3), 18);
//            } else {
//                Assert.fail("service is null");
//            }
//        } else {
//            Assert.fail("service ref is null");
//        }
//    }

    public static void globalWait(BundleContext context) throws Exception {
        OSGiUtils.waitForActiveBundle(context, "org.jboss.weld.osgi.weld-osgi-integration-bundle-api");
        OSGiUtils.waitForActiveBundle(context, "org.jboss.weld.osgi.weld-osgi-integration-bundle1");
        OSGiUtils.waitForActiveBundle(context, "org.jboss.weld.osgi.weld-osgi-integration-bundle2");
    }
}
