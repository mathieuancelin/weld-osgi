package org.osgi.cdi.test.service;

import com.sample.osgi.bundle1.api.PropertyService;
import com.sample.osgi.bundle1.api.TestPublished;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class MetaFilterTest {

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
    public void metaFilterTest(BundleContext context) throws InterruptedException, InvalidSyntaxException, BundleException {
        Environment.waitForEnvironment(context);
        ServiceReference ref = context.getServiceReference(TestPublished.class.getName());
        TestPublished test = (TestPublished) context.getService(ref);
        if (test != null) {
            PropertyService serv1 = test.getService();
            PropertyService serv2 = test.getService2();
            Assert.assertNotNull(serv1.whoAmI());
            Assert.assertNotNull(serv2.whoAmI());
            Assert.assertTrue(serv1.whoAmI().equals(serv2.whoAmI()));
        } else {
            Assert.fail("No test bean available");
        }
    }
}
