package org.osgi.cdi.test.service;

import com.sample.osgi.bundle1.api.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.*;

import java.io.Serializable;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class ServicePublishingTest {

    @Configuration
    public static Option[] configure() {
        return options(
                Environment.CDIOSGiEnvironment(
                        mavenBundle("com.sample.osgi","cdi-osgi-tests-bundle1").version("1.0-SNAPSHOT"),
                        mavenBundle("com.sample.osgi","cdi-osgi-tests-bundle2").version("1.0-SNAPSHOT"),
                        mavenBundle("com.sample.osgi","cdi-osgi-tests-bundle3").version("1.0-SNAPSHOT")
                )
        );
    }

    @Test
    //@Ignore
    public void servicePublishingTest(BundleContext context) throws InterruptedException, InvalidSyntaxException, BundleException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null, bundle2 = null, bundle3 = null;

        for(Bundle b : context.getBundles()) {
            Assert.assertEquals("Bundle " + b.getSymbolicName() + " is not ACTIVE but " + Environment.state(b.getState()), Bundle.ACTIVE, b.getState());
            if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle1")) {
                bundle1=b;
            }
            else if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle2")) {
                bundle2=b;
            }
            else if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle3")) {
                bundle3=b;
            }
        }

        ServiceReference[] autoPublishedServiceReferences = context.getServiceReferences(AutoPublishedService.class.getName(),null);
        AutoPublishedService autoPublishedService1 = null;
        AutoPublishedService autoPublishedService2 = null;
        Assert.assertNotNull("The auto published service reference array was null",autoPublishedServiceReferences);
        Assert.assertEquals("The number of auto published service implementations was wrong",2,autoPublishedServiceReferences.length);
        for(ServiceReference ref : autoPublishedServiceReferences) {
            if(ref.getBundle() == bundle1) {
                autoPublishedService1 = (AutoPublishedService)context.getService(ref);
            }
            else if(ref.getBundle() == bundle2) {
                autoPublishedService2 = (AutoPublishedService)context.getService(ref);
            }
        }
        Assert.assertNotNull("The auto published service 1 was null",autoPublishedService1);
        Assert.assertNotNull("The auto published service 2 was null",autoPublishedService2);
        Assert.assertEquals("The auto published service 1 method result was wrong","com.sample.osgi.bundle1.impl.AutoPublishedServiceImpl",autoPublishedService1.whoAmI());
        Assert.assertEquals("The auto published service 2 method result was wrong","com.sample.osgi.bundle2.impl.AutoPublishedServiceImpl",autoPublishedService2.whoAmI());

        ServiceReference[] manualPublishedServiceReferences = context.getServiceReferences(ManualPublishedService.class.getName(),null);
        ManualPublishedService manualPublishedService1 = null;
        ManualPublishedService manualPublishedService2 = null;
        ManualPublishedService manualPublishedService3 = null;
        Assert.assertNotNull("The manual published service reference array was null",manualPublishedServiceReferences);
        Assert.assertEquals("The number of manual published service implementations was wrong",3,manualPublishedServiceReferences.length);
        for(ServiceReference ref : manualPublishedServiceReferences) {
            if(ref.getBundle() == bundle1) {
                manualPublishedService1 = (ManualPublishedService)context.getService(ref);
            }
            else if(ref.getBundle() == bundle2) {
                manualPublishedService2 = (ManualPublishedService)context.getService(ref);
            }
            else if(ref.getBundle() == bundle3) {
                manualPublishedService3 = (ManualPublishedService)context.getService(ref);
            }
        }
        Assert.assertNotNull("The manual published service 1 was null",manualPublishedService1);
        Assert.assertNotNull("The manual published service 2 was null",manualPublishedService2);
        Assert.assertNotNull("The manual published service 3 was null",manualPublishedService3);
        Assert.assertEquals("The manual published service 1 method result was wrong","com.sample.osgi.bundle1.impl.ManualPublishedServiceImpl",manualPublishedService1.whoAmI());
        Assert.assertEquals("The manual published service 2 method result was wrong","com.sample.osgi.bundle2.impl.ManualPublishedServiceImpl",manualPublishedService2.whoAmI());
        Assert.assertEquals("The manual published service 3 method result was wrong","com.sample.osgi.bundle3.impl.ManualPublishedServiceImpl",manualPublishedService3.whoAmI());

        ServiceReference[] contractPublishedServiceReferences = context.getServiceReferences(ContractInterface.class.getName(),null);
        ContractInterface contractPublishedService = null;
        Assert.assertNotNull("The contract published service reference array was null",contractPublishedServiceReferences);
        Assert.assertEquals("The number of contract published service implementations was wrong", 1,contractPublishedServiceReferences.length);
        for(ServiceReference ref : contractPublishedServiceReferences) {
            if(ref.getBundle() == bundle1) {
                contractPublishedService = (ContractInterface)context.getService(ref);
            }
        }
        Assert.assertNotNull("The contract published service was null", contractPublishedService);
        ServiceReference[] notNontractPublishedServiceReferences = context.getServiceReferences(NotContractInterface.class.getName(),null);
        Assert.assertNull("The not contract published service reference array was not null", notNontractPublishedServiceReferences);

        ServiceReference[] blackListedServiceReferences = context.getServiceReferences(Serializable.class.getName(),null);
        Assert.assertNotNull("The black list service reference array was null",blackListedServiceReferences);
        Assert.assertEquals("The number of unblacklisted service implementations was wrong", 1, blackListedServiceReferences.length);
        Serializable unblackListedService = null;
        for(ServiceReference ref : blackListedServiceReferences) {
            if(ref.getBundle() == bundle1) {
                unblackListedService = (Serializable)context.getService(ref);
            }
        }
        Assert.assertNotNull("The unblacklisted published service was null",unblackListedService);

        ServiceReference[] propertyServiceReferences = context.getServiceReferences(PropertyService.class.getName(),null);
        PropertyService propertyService1 = null;
        PropertyService propertyService2 = null;
        PropertyService propertyService3 = null;
        Assert.assertNotNull("The property service reference array was null",propertyServiceReferences);
        Assert.assertEquals("The number of property service implementations was wrong", 3,propertyServiceReferences.length);
        for(ServiceReference ref : propertyServiceReferences) {
            if(ref.getProperty("Name.value") == null) {
                propertyService1 = (PropertyService)context.getService(ref);
            }
            else if(ref.getProperty("Name.value").equals("1")) {
                propertyService2 = (PropertyService)context.getService(ref);
            }
            else if(ref.getProperty("Name.value").equals("2")) {
                propertyService3 = (PropertyService)context.getService(ref);
            }
        }
        Assert.assertNotNull("The property service 1 was null",propertyService1);
        Assert.assertNotNull("The property service 2 was null",propertyService2);
        Assert.assertNotNull("The property service 3 was null",propertyService3);
        Assert.assertEquals("The property service 1 method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl1",propertyService1.whoAmI());
        Assert.assertEquals("The property service 2 method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl2",propertyService2.whoAmI());
        Assert.assertEquals("The property service 3 method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl3",propertyService3.whoAmI());
    }
}
