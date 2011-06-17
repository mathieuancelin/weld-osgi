package org.osgi.cdi.test;

import com.sample.osgi.bundle1.api.*;
import com.sample.osgi.bundle1.impl.PropertyServiceImpl2;
import com.sample.osgi.bundle1.util.EventListener;
import com.sample.osgi.bundle1.util.ServiceProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.*;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.io.Serializable;
import java.util.Collection;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class UsageTest {

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
    public void launchTest(BundleContext context) throws InterruptedException, BundleException, InvalidSyntaxException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null, bundle2 = null, bundle3 = null;
        for(Bundle b : context.getBundles()) {
            Assert.assertEquals("Bundle" + b.getSymbolicName() + "is not ACTIVE", Bundle.ACTIVE, b.getState());
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
        Assert.assertNotNull("The bundle1 was not retrieved",bundle1);
        Assert.assertNotNull("The bundle2 was not retrieved",bundle2);
        Assert.assertNotNull("The bundle3 was not retrieved",bundle3);

        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);
        Collection<CDIContainer> containers = factory.containers();

        Assert.assertEquals("The container collection had the wrong number of containers",2,containers.size());

        CDIContainer container1 = factory.container(bundle1);
        CDIContainer container2 = factory.container(bundle2);
        CDIContainer container3 = factory.container(bundle3);
        Assert.assertNotNull("The container for bundle1 was null",container1);
        Assert.assertNotNull("The container for bundle2 was null",container2);
        Assert.assertNull("The container for bundle3 was not null", container3);
        Assert.assertTrue("The container for bundle1 was not started",container1.isStarted());
        Assert.assertTrue("The container for bundle2 was not started",container2.isStarted());

        Collection<ServiceRegistration> registrations1 = container1.getRegistrations();
        Collection<ServiceRegistration> registrations2 = container2.getRegistrations();
        Assert.assertEquals("The registration collection 1 had the wrong number of registrations",3,registrations1.size());
        Assert.assertEquals("The registration collection 2 had the wrong number of registrations",3,registrations2.size());

        Collection<String> beanClasses1 = container1.getBeanClasses();
        Collection<String> beanClasses2 = container2.getBeanClasses();
        Assert.assertNotNull("The bean class collection 1 was null",beanClasses1);
        Assert.assertNotNull("The bean class collection 2 was null",beanClasses2);

        BeanManager beanManager1 = container1.getBeanManager();
        BeanManager beanManager2 = container2.getBeanManager();
        ServiceReference[] beanManagerServices = context.getServiceReferences(BeanManager.class.getName(),null);
        Assert.assertNotNull("The event bean manager reference array was null",beanManagerServices);
        Assert.assertEquals("The number of bean manager services was wrong", 2, beanManagerServices.length);
        Assert.assertNotNull("The bean manager 1 was null", beanManager1);
        Assert.assertNotNull("The bean manager 2 was null", beanManager2);

        Event event1 = container1.getEvent();
        Event event2 = container2.getEvent();
        ServiceReference[] eventServices = context.getServiceReferences(Event.class.getName(),null);
        Assert.assertNotNull("The event service reference array was null",eventServices);
        Assert.assertEquals("The number of event services was wrong",2,eventServices.length);
        Assert.assertNotNull("The event 1 was null",event1);
        Assert.assertNotNull("The event 2 was null",event2);

        Instance instance1 = container1.getInstance();
        Instance instance2 = container2.getInstance();
        ServiceReference[] instanceServices = context.getServiceReferences(Instance.class.getName(),null);
        Assert.assertNotNull("The instance service reference array was null",instanceServices);
        Assert.assertEquals("The number of instance services was wrong",2,instanceServices.length);
        Assert.assertNotNull("The instance 1 was null",instance1);
        Assert.assertNotNull("The instance 2 was null",instance2);

        Assert.assertTrue("The container was not been shutdown",container1.shutdown());
        Assert.assertFalse("The container was still started",container1.isStarted());
        Assert.assertEquals("The container collection had the wrong number of containers",2,containers.size());

    }

    @Test
    public void servicePublishingTest(BundleContext context) throws InterruptedException, InvalidSyntaxException, BundleException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null, bundle2 = null, bundle3 = null;

        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);

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

    @Test
    public void serviceConsumingTest(BundleContext context) throws InterruptedException, InvalidSyntaxException {
        Environment.waitForEnvironment(context);

        ServiceReference[] serviceProviderReferences = context.getServiceReferences(ServiceProvider.class.getName(),null);
        Assert.assertNotNull("The service provider reference array was null",serviceProviderReferences);
        Assert.assertEquals("The number of service provider implementations was wrong", 1,serviceProviderReferences.length);
        ServiceProvider serviceProvider = (ServiceProvider)context.getService(serviceProviderReferences[0]);
        Assert.assertNotNull("The service provider was null",serviceProvider);

        PropertyService service = serviceProvider.getService();
        Assert.assertNotNull("The service was null", service);
        Assert.assertEquals("The service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl1",service.whoAmI());
        PropertyService filteredService = serviceProvider.getFilteredService();
        Assert.assertNotNull("The filtered service was null", filteredService);
        Assert.assertEquals("The filtered service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl2",filteredService.whoAmI());
        PropertyService qualifiedService = serviceProvider.getQualifiedService();
        Assert.assertNotNull("The qualified service was null", qualifiedService);
        Assert.assertEquals("The qualified service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl3",qualifiedService.whoAmI());
        PropertyService filteredFromQualifierService = serviceProvider.getFilteredFromQualifierService();
        Assert.assertNotNull("The filtered from qualifier service was null", filteredFromQualifierService);
        Assert.assertEquals("The filtered from qualifier service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl3",filteredFromQualifierService.whoAmI());
        PropertyService qualifiedFromPropertyService = serviceProvider.getQualifiedFromPropertyService();
        Assert.assertNotNull("The qualified from property service was null", qualifiedFromPropertyService);
        Assert.assertEquals("The qualified from property service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl2",qualifiedFromPropertyService.whoAmI());
        PropertyService otherFilteredService = serviceProvider.getOtherFilteredService();
        Assert.assertNotNull("The other filtered service was null", otherFilteredService);
        Assert.assertEquals("The other service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl2",otherFilteredService.whoAmI());

//        Service<PropertyService> services = serviceProvider.getServices();
//        Assert.assertNotNull("The service provider was null", services);
//        Assert.assertEquals("The service provider method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl1",services.get().whoAmI());
//        Service<PropertyService> filteredServices = serviceProvider.getFilteredServices();
//        Assert.assertNotNull("The filtered provider service was null", filteredServices);
//        Assert.assertEquals("The filtered service provider method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl2",filteredServices.get().whoAmI());
//        Service<PropertyService> qualifiedServices = serviceProvider.getQualifiedServices();
//        Assert.assertNotNull("The qualified service provider was null", qualifiedServices);
//        Assert.assertEquals("The qualified service provider method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl3",qualifiedServices.get().whoAmI());
//        Service<PropertyService> filteredFromQualifierServices = serviceProvider.getFilteredFromQualifierServices();
//        Assert.assertNotNull("The filtered from qualifier service provider was null", filteredFromQualifierServices);
//        Assert.assertEquals("The filtered from qualifier service provider method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl3",filteredFromQualifierServices.get().whoAmI());
//        Service<PropertyService> qualifiedFromPropertyServices = serviceProvider.getQualifiedFromPropertyServices();
//        Assert.assertNotNull("The qualified from property service was null", qualifiedFromPropertyServices);
//        Assert.assertEquals("The qualified from property service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl2",qualifiedFromPropertyServices.get().whoAmI());
//        Service<PropertyService> otherFilteredServices = serviceProvider.getOtherFilteredServices();
//        Assert.assertNotNull("The other filtered service was null", otherFilteredServices);
//        Assert.assertEquals("The other service method result was wrong","com.sample.osgi.bundle1.impl.PropertyServiceImpl2",otherFilteredServices.get().whoAmI());
    }

    @Test
    public void eventTest(BundleContext context) throws InterruptedException, InvalidSyntaxException, BundleException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null, bundle2 = null, bundle3 = null;
        for(Bundle b : context.getBundles()) {
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
        Assert.assertNotNull("The bundle1 was not retrieved",bundle1);
        Assert.assertNotNull("The bundle2 was not retrieved",bundle2);
        Assert.assertNotNull("The bundle3 was not retrieved",bundle3);

        ServiceReference[] eventListenerReferences = context.getServiceReferences(EventListener.class.getName(),null);
        Assert.assertNotNull("The event listener reference array was null",eventListenerReferences);
        Assert.assertEquals("The number of event listener implementations was wrong", 1,eventListenerReferences.length);
        EventListener eventListener = (EventListener)context.getService(eventListenerReferences[0]);
        Assert.assertNotNull("The event listener was null",eventListener);

        Assert.assertEquals("The number of listened BundleContainerInitialized event was wrong",1,eventListener.getStart());
        Assert.assertEquals("The number of listened BundleContainerShutdown event was wrong",0,eventListener.getStop());

        int serviceArrival = eventListener.getServiceArrival();
        int serviceChanged = eventListener.getServiceChanged();
        int serviceDeparture = eventListener.getServiceDeparture();
        Assert.assertTrue("The number of listened ServiceArrival event was wrong",serviceArrival > 0);
        Assert.assertEquals("The number of listened ServiceChanged event was wrong", 0, serviceChanged);
        Assert.assertEquals("The number of listened ServiceDeparture event was wrong", 0, serviceDeparture);

        ServiceRegistration registration = context.registerService(MovingService.class.getName(), eventListener.getMovingServiceInstance(), null);
        Assert.assertEquals("The new number of listened ServiceArrival event was wrong", serviceArrival + 1, eventListener.getServiceArrival());
        Assert.assertEquals("The new number of listened ServiceChanged event was wrong", serviceChanged, eventListener.getServiceChanged());
        Assert.assertEquals("The new number of listened ServiceDeparture event was wrong", serviceDeparture, eventListener.getServiceDeparture());

        registration.setProperties(null);
        Assert.assertEquals("The new number of listened ServiceArrival event was wrong", serviceArrival + 1, eventListener.getServiceArrival());
        Assert.assertEquals("The new number of listened ServiceChanged event was wrong", serviceChanged + 1, eventListener.getServiceChanged());
        Assert.assertEquals("The new number of listened ServiceDeparture event was wrong", serviceDeparture, eventListener.getServiceDeparture());

        registration.unregister();
        Assert.assertEquals("The new number of listened ServiceArrival event was wrong", serviceArrival + 1, eventListener.getServiceArrival());
        Assert.assertEquals("The new number of listened ServiceChanged event was wrong", serviceChanged + 1, eventListener.getServiceChanged());
        Assert.assertEquals("The new number of listened ServiceDeparture event was wrong", serviceDeparture + 1, eventListener.getServiceDeparture());

        int bundleInstalled = eventListener.getBundleInstalled();
        int bundleUninstalled = eventListener.getBundleUninstalled();
        int bundleResolved = eventListener.getBundleResolved();
        int bundleUnresolved = eventListener.getBundleUnresolved();
        int bundleStarting = eventListener.getBundleStarting();
        int bundleStarted = eventListener.getBundleStarted();
        int bundleStopping = eventListener.getBundleStopping();
        int bundleStopped = eventListener.getBundleStopped();
        int bundleUpdated = eventListener.getBundleUpdated();
        int bundleLazyActivation = eventListener.getBundleLazyActivation();
        Assert.assertEquals("The number of listened BundleInstalled event was wrong", 0, bundleInstalled);
        Assert.assertEquals("The number of listened BundleUninstalled event was wrong", 0, bundleUninstalled);
        Assert.assertEquals("The number of listened BundleResolved event was wrong", 2, bundleResolved);
        Assert.assertEquals("The number of listened BundleUnresolved event was wrong", 0, bundleUnresolved);
        Assert.assertEquals("The number of listened BundleStarting event was wrong", 0, bundleStarting);
        Assert.assertEquals("The number of listened BundleStarted event was wrong", 2, bundleStarted);
        Assert.assertEquals("The number of listened BundleStopping event was wrong", 0, bundleStopping);
        Assert.assertEquals("The number of listened BundleStopped event was wrong", 0, bundleStopped);
        Assert.assertEquals("The number of listened BundleUpdated event was wrong", 0, bundleUpdated);
        Assert.assertEquals("The number of listened BundleLazyActivation event was wrong", 0, bundleLazyActivation);

        bundle3.stop();
        Environment.waitForState(bundle3,Bundle.RESOLVED);
        Assert.assertEquals("The new number of listened BundleInstalled event was wrong", bundleInstalled, eventListener.getBundleInstalled());
        Assert.assertEquals("The new number of listened BundleUninstalled event was wrong", bundleUninstalled, eventListener.getBundleUninstalled());
        Assert.assertEquals("The new number of listened BundleResolved event was wrong", bundleResolved, eventListener.getBundleResolved());
        Assert.assertEquals("The new number of listened BundleUnresolved event was wrong", bundleUnresolved, eventListener.getBundleUnresolved());
        Assert.assertEquals("The new number of listened BundleStarting event was wrong", bundleStarting, eventListener.getBundleStarting());
        Assert.assertEquals("The new number of listened BundleStarted event was wrong", bundleStarted, eventListener.getBundleStarted());
//        Assert.assertEquals("The new number of listened BundleStopping event was wrong", bundleStopping + 1, eventListener.getBundleStopping());
        Assert.assertEquals("The new number of listened BundleStopped event was wrong", bundleStopped + 1, eventListener.getBundleStopped());
        Assert.assertEquals("The new number of listened BundleUpdated event was wrong", bundleUpdated, eventListener.getBundleUpdated());
        Assert.assertEquals("The new number of listened BundleLazyActivation event was wrong", bundleLazyActivation, eventListener.getBundleLazyActivation());

        bundle3.start();
        Environment.waitForState(bundle3,Bundle.ACTIVE);
        Assert.assertEquals("The new number of listened BundleInstalled event was wrong", bundleInstalled, eventListener.getBundleInstalled());
        Assert.assertEquals("The new number of listened BundleUninstalled event was wrong", bundleUninstalled, eventListener.getBundleUninstalled());
        Assert.assertEquals("The new number of listened BundleResolved event was wrong", bundleResolved, eventListener.getBundleResolved());
        Assert.assertEquals("The new number of listened BundleUnresolved event was wrong", bundleUnresolved, eventListener.getBundleUnresolved());
//        Assert.assertEquals("The new number of listened BundleStarting event was wrong", bundleStarting + 1, eventListener.getBundleStarting());
        Assert.assertEquals("The new number of listened BundleStarted event was wrong", bundleStarted + 1, eventListener.getBundleStarted());
//        Assert.assertEquals("The new number of listened BundleStopping event was wrong", bundleStopping + 1, eventListener.getBundleStopping());
        Assert.assertEquals("The new number of listened BundleStopped event was wrong", bundleStopped + 1, eventListener.getBundleStopped());
        Assert.assertEquals("The new number of listened BundleUpdated event was wrong", bundleUpdated, eventListener.getBundleUpdated());
        Assert.assertEquals("The new number of listened BundleLazyActivation event was wrong", bundleLazyActivation, eventListener.getBundleLazyActivation());

        bundle3.update();
        Environment.waitForState(bundle3,Bundle.ACTIVE);
        Assert.assertEquals("The new number of listened BundleInstalled event was wrong", bundleInstalled, eventListener.getBundleInstalled());
        Assert.assertEquals("The new number of listened BundleUninstalled event was wrong", bundleUninstalled, eventListener.getBundleUninstalled());
        Assert.assertEquals("The new number of listened BundleResolved event was wrong", bundleResolved + 1, eventListener.getBundleResolved());
        Assert.assertEquals("The new number of listened BundleUnresolved event was wrong", bundleUnresolved + 1, eventListener.getBundleUnresolved());
//        Assert.assertEquals("The new number of listened BundleStarting event was wrong", bundleStarting + 2, eventListener.getBundleStarting());
        Assert.assertEquals("The new number of listened BundleStarted event was wrong", bundleStarted + 2, eventListener.getBundleStarted());
//        Assert.assertEquals("The new number of listened BundleStopping event was wrong", bundleStopping + 2, eventListener.getBundleStopping());
        Assert.assertEquals("The new number of listened BundleStopped event was wrong", bundleStopped + 2, eventListener.getBundleStopped());
        Assert.assertEquals("The new number of listened BundleUpdated event was wrong", bundleUpdated + 1, eventListener.getBundleUpdated());
        Assert.assertEquals("The new number of listened BundleLazyActivation event was wrong", bundleLazyActivation, eventListener.getBundleLazyActivation());

        String location = bundle3.getLocation();
        bundle3.uninstall();
        Environment.waitForState(bundle3,Bundle.UNINSTALLED);
        Assert.assertEquals("The new number of listened BundleInstalled event was wrong", bundleInstalled, eventListener.getBundleInstalled());
        Assert.assertEquals("The new number of listened BundleUninstalled event was wrong", bundleUninstalled + 1, eventListener.getBundleUninstalled());
        Assert.assertEquals("The new number of listened BundleResolved event was wrong", bundleResolved + 1, eventListener.getBundleResolved());
        Assert.assertEquals("The new number of listened BundleUnresolved event was wrong", bundleUnresolved + 2, eventListener.getBundleUnresolved());
//        Assert.assertEquals("The new number of listened BundleStarting event was wrong", bundleStarting + 2, eventListener.getBundleStarting());
        Assert.assertEquals("The new number of listened BundleStarted event was wrong", bundleStarted + 2, eventListener.getBundleStarted());
//        Assert.assertEquals("The new number of listened BundleStopping event was wrong", bundleStopping + 3, eventListener.getBundleStopping());
        Assert.assertEquals("The new number of listened BundleStopped event was wrong", bundleStopped + 3, eventListener.getBundleStopped());
        Assert.assertEquals("The new number of listened BundleUpdated event was wrong", bundleUpdated + 1, eventListener.getBundleUpdated());
        Assert.assertEquals("The new number of listened BundleLazyActivation event was wrong", bundleLazyActivation, eventListener.getBundleLazyActivation());

        context.installBundle(location);
        Thread.sleep(500);
        Assert.assertEquals("The new number of listened BundleInstalled event was wrong", bundleInstalled + 1, eventListener.getBundleInstalled());
        Assert.assertEquals("The new number of listened BundleUninstalled event was wrong", bundleUninstalled + 1, eventListener.getBundleUninstalled());
        Assert.assertEquals("The new number of listened BundleResolved event was wrong", bundleResolved + 1, eventListener.getBundleResolved());
        Assert.assertEquals("The new number of listened BundleUnresolved event was wrong", bundleUnresolved + 2, eventListener.getBundleUnresolved());
//        Assert.assertEquals("The new number of listened BundleStarting event was wrong", bundleStarting + 2, eventListener.getBundleStarting());
        Assert.assertEquals("The new number of listened BundleStarted event was wrong", bundleStarted + 2, eventListener.getBundleStarted());
//        Assert.assertEquals("The new number of listened BundleStopping event was wrong", bundleStopping + 3, eventListener.getBundleStopping());
        Assert.assertEquals("The new number of listened BundleStopped event was wrong", bundleStopped + 3, eventListener.getBundleStopped());
        Assert.assertEquals("The new number of listened BundleUpdated event was wrong", bundleUpdated + 1, eventListener.getBundleUpdated());
        Assert.assertEquals("The new number of listened BundleLazyActivation event was wrong", bundleLazyActivation, eventListener.getBundleLazyActivation());
    }
}
