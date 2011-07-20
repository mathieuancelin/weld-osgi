package org.osgi.cdi.test.framework;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.api.extension.BundleState;
import org.osgi.cdi.api.extension.annotation.BundleDataFile;
import org.osgi.cdi.api.extension.events.AbstractBundleContainerEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class InfrastructureTest {

    @Configuration
    public static Option[] configure() {
        return options(
                Environment.CDIOSGiEnvironment(
                        mavenBundle("org.osgi.cdi", "cdi-osgi-tests-importing-bundle").version("1.0-SNAPSHOT")
                                              )
        );
    }

    @Test
    //@Ignore
    public void fiveBundlesTest(BundleContext context) throws InterruptedException, BundleException {
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
        Assert.assertNotNull("Extension API bundle is not present", extAPI);
        Assert.assertNotNull("Integration API bundle is not present",intAPI);
        Assert.assertNotNull("Extension Impl bundle is not present",extImpl);
        Assert.assertNotNull("Integration Impl bundle is not present",intImpl);
        Assert.assertNotNull("Mandatory bundle is not present",mand);
    }

    @Test
    //@Ignore
    public void interactionsTest(BundleContext context) throws InterruptedException {
        Environment.waitForEnvironment(context);

        Bundle importingBundle = null;
        for(Bundle b : context.getBundles()) {
            if(b.getSymbolicName().equals("org.osgi.cdi.osgi-tests-importing-bundle")) {
                importingBundle=b;
            }
        }
        Assert.assertNotNull("Importing bundle is not present", importingBundle);

        try {
            Assert.assertEquals("Unable to access the package org.osgi.cdi.api.extension", "org.osgi.cdi.api.extension", importingBundle.loadClass(BundleState.class.getName()).getPackage().getName());
        } catch (ClassNotFoundException e) {
            Assert.fail("Unable to access the package org.osgi.cdi.api.extension" + e.getMessage());
        }
        try {
            Assert.assertEquals("Unable to access the package org.osgi.cdi.api.extension.annotation", "org.osgi.cdi.api.extension.annotation", importingBundle.loadClass(BundleDataFile.class.getName()).getPackage().getName());
        } catch (ClassNotFoundException e) {
            Assert.fail("Unable to access the package org.osgi.cdi.api.extension.annotation" + e.getMessage());
        }
        try {
            Assert.assertEquals("Unable to access the package org.osgi.cdi.api.extension.events", "org.osgi.cdi.api.extension.events", importingBundle.loadClass(AbstractBundleContainerEvent.class.getName()).getPackage().getName());
        } catch (ClassNotFoundException e) {
            Assert.fail("Unable to access the package org.osgi.cdi.api.extension.events" + e.getMessage());
        }

        try {
            Assert.assertEquals("Unable to access the package org.osgi.cdi.api.integration", "org.osgi.cdi.api.integration", importingBundle.loadClass(CDIContainer.class.getName()).getPackage().getName());
        } catch (ClassNotFoundException e) {
            Assert.fail("Unable to access the package org.osgi.cdi.api.integration" + e.getMessage());
        }
    }
}
