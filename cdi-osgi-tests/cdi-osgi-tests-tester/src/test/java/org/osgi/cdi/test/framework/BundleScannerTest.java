package org.osgi.cdi.test.framework;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.*;

import java.util.Collection;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class BundleScannerTest  {

    @Configuration
    public static Option[] configure() {
        return options(
                Environment.CDIOSGiEnvironment(
                        mavenBundle("com.sample.osgi", "cdi-osgi-tests-scanner").version("1.0-SNAPSHOT"),
                        mavenBundle("com.sample.osgi", "cdi-osgi-tests-scanner-inner").version("1.0-SNAPSHOT")
                                              )
        );
    }

    @Test
    //@Ignore
    public void bundleScannerTest(BundleContext context) throws InterruptedException, BundleException, InvalidSyntaxException {
        Environment.waitForEnvironment(context);

        Bundle bundleScanner = null, bundleScannerInner = null;
        for(Bundle b : context.getBundles()) {
            Assert.assertEquals("Bundle" + b.getSymbolicName() + " is not ACTIVE", Bundle.ACTIVE, b.getState());
            if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-scanner")) {
                bundleScanner=b;
            }
            else if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-scanner-inner")) {
                bundleScannerInner=b;
            }
        }
        Assert.assertNotNull("The bundleScanner was not retrieved",bundleScanner);
        Assert.assertNotNull("The bundleScannerInner was not retrieved",bundleScannerInner);
        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);
        Collection<CDIContainer> containers = factory.containers();

        Assert.assertEquals("The container collection had the wrong number of containers",2,containers.size());

        CDIContainer container1 = factory.container(bundleScanner);
        CDIContainer container2 = factory.container(bundleScannerInner);
        Assert.assertNotNull("The container for bundleScanner was null",container1);
        Assert.assertNotNull("The container for bundleScannerInner was null", container2);
        Assert.assertTrue("The container for bundleScanner was not started",container1.isStarted());
        Assert.assertTrue("The container for bundleScannerInner was not started",container2.isStarted());

        Collection<String> classScanner = container1.getBeanClasses();
        Collection<String> classScannerInner = container2.getBeanClasses();
        Assert.assertNotNull("The bean class collection for bundleScanner was null",classScanner);
        Assert.assertNotNull("The bean class collection for bundleScannerInner was null", classScannerInner);
        Assert.assertEquals("The bean class collection size for bundleScanner was wrong",2,classScanner.size());
        Assert.assertEquals("The bean class collection size for bundleScannerInner was wrong",1,classScannerInner.size());
        Assert.assertTrue("The class com.sample.ScannerClass was not registered for bundleScanner", classScanner.contains("com.sample.ScannerClass"));
        Assert.assertTrue("The class com.sample.ScannerInnerClass was not registered for bundleScanner", classScanner.contains("com.sample.ScannerInnerClass"));
        Assert.assertTrue("The class com.sample.ScannerInnerClass was not registered for bundleScanner",classScannerInner.contains("com.sample.ScannerInnerClass"));
    }
}
