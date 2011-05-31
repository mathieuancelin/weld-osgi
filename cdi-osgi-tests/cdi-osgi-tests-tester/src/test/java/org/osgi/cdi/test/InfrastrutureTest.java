package org.osgi.cdi.test;

import junit.framework.Assert;
import org.ops4j.pax.exam.Option;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.ops4j.pax.exam.CoreOptions.options;

//@RunWith(JUnit4TestRunner.class)
public class InfrastrutureTest {

//    @Configuration
    public static Option[] configure() {
        return options(
                Environment.CDIOSGiEnvironment()
        );
    }

//    @Test
    public void launchTest(BundleContext context) throws InterruptedException, BundleException {
        Environment.waitForEnvironment(context);

        Bundle extAPI = null, intAPI = null, extImpl = null, intImpl = null, mand = null;
        for(Bundle b : context.getBundles()) {
            if(b.getSymbolicName().equals("org.osgi.cdi.osgi-extension-api")) {
                extAPI=b;
            } else if(b.getSymbolicName().equals("org.osgi.cdi.osgi-integration-api")) {
                intAPI=b;
            } else if(b.getSymbolicName().equals("org.osgi.cdi.osgi-extension-impl")) {
                extImpl=b;
            } else if(b.getSymbolicName().equals("org.jboss.weld.osgi.weld-osgi-impl")) {
                intImpl=b;
            } else if(b.getSymbolicName().equals("org.osgi.cdi.osgi-mandatory")) {
                mand=b;
            }
        }
        Assert.assertNotNull("Extension API bundle is not present",extAPI);
        Assert.assertNotNull("Integration API bundle is not present",intAPI);
        Assert.assertNotNull("Extension Impl bundle is not present",extImpl);
        Assert.assertNotNull("Integration Impl bundle is not present",intImpl);
        Assert.assertNotNull("Mandatory bundle is not present",mand);

        Assert.assertEquals("Extension API bundle is not ACTIVE",Bundle.ACTIVE,extAPI.getState());
        Assert.assertEquals("Integration API bundle is not ACTIVE",Bundle.ACTIVE,intAPI.getState());
        Assert.assertEquals("Extension Impl bundle is not ACTIVE",Bundle.ACTIVE,extImpl.getState());
        Assert.assertEquals("Integration Impl bundle is not ACTIVE",Bundle.ACTIVE,intImpl.getState());
        Assert.assertEquals("Mandatory bundle is not ACTIVE",Bundle.ACTIVE,mand.getState());

        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        Assert.assertNotNull("CDI container factory service is not available",factoryReference);
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);
        Assert.assertNotNull("CDI container factory is not available",factory);

    }

//    @Test
    public void CDIContainerFactoryTest(BundleContext context) throws InterruptedException {
        Environment.waitForEnvironment(context);

        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);

        Collection<CDIContainer> containers = factory.containers();
        Assert.assertNotNull("The container collection was null",containers);
        Assert.assertEquals("The container collection was not empty",0,containers.size());

        CDIContainer container;
        int i = 0;
        for(Bundle b : context.getBundles()) {
            container = factory.container(b);
            Assert.assertNull("The bundle already got a container",container);
            container = factory.createContainer(b);
            Assert.assertNotNull("The container creation failed", container);
            Assert.assertNull("The container was already added in container collection",factory.container(b));
            factory.addContainer(container);
            container = factory.container(b);
            Assert.assertNotNull("The container cannot be retrieved",container);
            Assert.assertEquals("The container was not correctly added in container collection",++i,containers.size());
        }
        Assert.assertEquals("Too much or too less registered containers",context.getBundles().length,i);

        for(Bundle b : context.getBundles()) {
            factory.removeContainer(b);
            Assert.assertEquals("The container was not correctly removed from container collection",--i,containers.size());
        }
        Assert.assertEquals("There still containers in the container collection",0,i);

    }

//    @Test
    public void CDIContainerTest(BundleContext context) throws InterruptedException, InvalidSyntaxException {
        Environment.waitForEnvironment(context);

        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);
        Collection<CDIContainer> containers = factory.containers();
        CDIContainer container = factory.createContainer(context.getBundle());

        Assert.assertEquals("The container had the wrong bundle", context.getBundle(), container.getBundle());
        Assert.assertFalse("The container was declared as STARTED",container.isStarted());
        Assert.assertNotNull("The registration collection was null",container.getRegistrations());
        Assert.assertEquals("The registration collection was not empty",0,container.getRegistrations().size());

        Collection<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();
        container.setRegistrations(registrations);
        Assert.assertNotNull("The registration collection was null",container.getRegistrations());
        Assert.assertEquals("The registration collection was not empty",0,container.getRegistrations().size());

        ServiceRegistration registration = context.registerService(String.class.getName(),"STRING",null);
        registrations.add(registration);
        container.setRegistrations(registrations);
        Assert.assertNotNull("The registration collection was null",container.getRegistrations());
        Assert.assertEquals("The registration collection had the wrong number of registration",1,container.getRegistrations().size());

        registration = context.registerService(String.class.getName(),"STRING2",null);
        registrations.add(registration);
        container.setRegistrations(registrations);
        Assert.assertNotNull("The registration collection was null",container.getRegistrations());
        Assert.assertEquals("The registration collection had the wrong number of registration",2,container.getRegistrations().size());

        registrations.clear();
        container.setRegistrations(registrations);
        Assert.assertNotNull("The registration collection was null",container.getRegistrations());
        Assert.assertEquals("The registration collection was not empty",0,container.getRegistrations().size());
        
    }
}
