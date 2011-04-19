package org.jboss.weld.osgi;

import com.sample.osgi.api.SomeService;
import org.osgi.framework.ServiceRegistration;
import com.sample.osgi.api.ServiceBundle2;
import java.util.Properties;
import org.jboss.weld.environment.osgi.api.extension.Registration;
import com.sample.osgi.api.DummyService;
import com.sample.osgi.api.ServiceBundle1;
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
    public void testServicesEvent(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle1.class.getName());
        if (ref != null) {
            ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref);
            if (service1 != null) {
//                Assert.assertEquals(service1.getStart(), 1);
                Assert.assertTrue(service1.getArrival() > 1);
                int oldArrival = service1.getArrival();
                int oldDeparture = service1.getDeparture();
//                int oldInvalid = service1.getInvalid();
                DummyService dummy = service1.getDummy();
                Registration<DummyService> reg = service1.getRegistry().registerService(DummyService.class, dummy);
//                Assert.assertEquals(service1.getValid(), 1);
                Assert.assertEquals(service1.getDummyArrival(), 1);
                Assert.assertEquals(context.getServiceReferences(DummyService.class.getName(), null).length, 1);
                Assert.assertEquals(service1.getArrival(), oldArrival + 1);
                Assert.assertEquals(service1.getRegistry().getServiceReferences(DummyService.class).size(), 1);
                reg.unregister();
//                Assert.assertEquals(service1.getInvalid(), oldInvalid + 1);
                Assert.assertNull(context.getServiceReferences(DummyService.class.getName(), null));
                Assert.assertEquals(service1.getDummyDeparture(), 1);
                Assert.assertEquals(service1.getDeparture(), oldDeparture + 1);
                Assert.assertEquals(service1.getRegistry().getServiceReferences(DummyService.class).size(), 0);
//                Assert.assertEquals(service1.getStop(), 0);
            } else {
                Assert.fail("service is null");
            }
        } else {
            Assert.fail("service ref is null");
        }
    }

    @Test
    public void testServicePublishing(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference[] ref = context.getServiceReferences(ServiceBundle1.class.getName(), null);
        ServiceReference[] ref2 = context.getServiceReferences(ServiceBundle2.class.getName(), null);
        Assert.assertNotNull(ref);
        Assert.assertNotNull(ref2);
        Assert.assertEquals(ref.length, 1);
        Assert.assertEquals(ref2.length, 1);
    }

    @Test
    public void testInterBundleEvent(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference[] ref = context.getServiceReferences(ServiceBundle1.class.getName(), null);
        ServiceReference[] ref2 = context.getServiceReferences(ServiceBundle2.class.getName(), null);
        Assert.assertNotNull(ref);
        Assert.assertNotNull(ref2);
        Assert.assertEquals(ref.length, 1);
        Assert.assertEquals(ref2.length, 1);
        ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref[0]);
        ServiceBundle2 service2 = (ServiceBundle2) context.getService(ref2[0]);
        Assert.assertEquals(service1.getIbEvent(), 0);
        Assert.assertEquals(service1.getIbEventAll(), 0);
        service2.fireLong();
        Assert.assertEquals(service1.getIbEvent(), 0);
        Assert.assertEquals(service1.getIbEventAll(), 1);
        service2.fireString();
        Assert.assertEquals(service1.getIbEvent(), 1);
        Assert.assertEquals(service1.getIbEventAll(), 2);
    }

    @Test
    public void testStartEvent(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle1.class.getName());
        if (ref != null) {
            ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref);
            if (service1 != null) {
                Assert.assertEquals(service1.getStart(), 1);
                Assert.assertEquals(service1.getStop(), 0);
            } else {
                Assert.fail("service is null");
            }
        } else {
            Assert.fail("service ref is null");
        }
    }

    @Test
    public void testDependenciesValidation(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle1.class.getName());
        if (ref != null) {
            ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref);
            if (service1 != null) {
                int oldInvalid = service1.getInvalid();
                DummyService dummy = service1.getDummy();
                Assert.assertEquals(service1.getValid(), 0);
                Registration<DummyService> reg = service1.getRegistry().registerService(DummyService.class, dummy);
                Assert.assertEquals(service1.getValid(), 1);
                reg.unregister();
                Assert.assertEquals(service1.getInvalid(), oldInvalid + 1);
            } else {
                Assert.fail("service is null");
            }
        } else {
            Assert.fail("service ref is null");
        }
    }

    @Test
    public void testServicesRegistry(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle1.class.getName());
        if (ref != null) {
            ServiceBundle1 service1 = (ServiceBundle1) context.getService(ref);
            if (service1 != null) {
                Assert.assertTrue(service1.getArrival() > 1);
                DummyService dummy = service1.getDummy();
                Registration<DummyService> reg = service1.getRegistry().registerService(DummyService.class, dummy);
                Assert.assertEquals(service1.getDummyArrival(), 1);
                Assert.assertEquals(context.getServiceReferences(DummyService.class.getName(), null).length, 1);
                Assert.assertEquals(service1.getRegistry().getServiceReferences(DummyService.class).size(), 1);
                reg.unregister();
                Assert.assertNull(context.getServiceReferences(DummyService.class.getName(), null));
                Assert.assertEquals(service1.getDummyDeparture(), 1);
                Assert.assertEquals(service1.getRegistry().getServiceReferences(DummyService.class).size(), 0);
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

    @Test
    public void testOSGiFilteredInjection(BundleContext context) throws Exception {
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

    @Test
    public void testFilteredServiceEvent(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle2.class.getName());
        if (ref != null) {
            ServiceBundle2 service1 = (ServiceBundle2) context.getService(ref);
            if (service1 != null) {
                Properties p = new Properties();
                p.setProperty("value", "fake");
                Assert.assertEquals(service1.getArrival(), 0);
                Assert.assertEquals(service1.getDeparture(), 0);
                ServiceRegistration reg = context.registerService(DummyService.class.getName(), new FakeDummy(), p);
                Assert.assertEquals(service1.getArrival(), 1);
                Assert.assertEquals(service1.getDeparture(), 0);
                reg.unregister();
                Assert.assertEquals(service1.getArrival(), 1);
                Assert.assertEquals(service1.getDeparture(), 1);
            } else {
                Assert.fail("service is null");
            }
        } else {
            Assert.fail("service ref is null");
        }
    }

    @Test
    public void testUnregistration(BundleContext context) throws Exception {
        globalWait(context);
        ServiceReference ref = context.getServiceReference(ServiceBundle2.class.getName());
        if (ref != null) {
            ServiceBundle2 service2 = (ServiceBundle2) context.getService(ref);
            if (service2 != null) {
                Assert.assertEquals(service2.getRegistry().getServiceReferences(SomeService.class).size(), 1);
                for (Registration<SomeService> reg : service2.getRegs()) {
                    reg.unregister();
                }
                Assert.assertEquals(service2.getRegistry().getServiceReferences(SomeService.class).size(), 0);
            } else {
                Assert.fail("service is null");
            }
        } else {
            Assert.fail("service ref is null");
        }
    }

    public static void globalWait(BundleContext context) throws Exception {
        OSGiUtils.waitForActiveBundle(context, "org.jboss.weld.osgi.weld-osgi-integration-bundle-api");
        OSGiUtils.waitForActiveBundle(context, "org.jboss.weld.osgi.weld-osgi-integration-bundle1");
        OSGiUtils.waitForActiveBundle(context, "org.jboss.weld.osgi.weld-osgi-integration-bundle2");
    }
}
