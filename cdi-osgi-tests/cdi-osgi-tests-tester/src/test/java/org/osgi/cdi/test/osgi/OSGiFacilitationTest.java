package org.osgi.cdi.test.osgi;

import com.sample.osgi.bundle1.util.BundleProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.util.Dictionary;
import java.util.Map;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class OSGiFacilitationTest {

    @Configuration
    public static Option[] configure() {
        return options(
                Environment.CDIOSGiEnvironment(
                        mavenBundle("com.sample.osgi", "cdi-osgi-tests-bundle1").version("1.0-SNAPSHOT"),
                        mavenBundle("com.sample.osgi", "cdi-osgi-tests-bundle2").version("1.0-SNAPSHOT"),
                        mavenBundle("com.sample.osgi", "cdi-osgi-tests-bundle3").version("1.0-SNAPSHOT")
                                              )
                      );
    }

    @Test
    //@Ignore
    public void osgiUtilitiesTest(BundleContext context) throws InterruptedException, InvalidSyntaxException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null, bundle2 = null;
        for(Bundle b : context.getBundles()) {
            if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle1")) {
                bundle1=b;
            } else if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle2")) {
                bundle2=b;
            }
        }

        ServiceReference[] bundleProviderServiceReferences = context.getServiceReferences(BundleProvider.class.getName(),null);
        Assert.assertNotNull("The bundle provider reference array was null", bundleProviderServiceReferences);
        Assert.assertEquals("The number of bundle provider implementations was wrong", 1,bundleProviderServiceReferences.length);
        BundleProvider bundleProvider = (BundleProvider)context.getService(bundleProviderServiceReferences[0]);
        Assert.assertNotNull("The bundle provider was null",bundleProvider);

        Bundle injectedBundle = bundleProvider.getBundle();
        Assert.assertNotNull("The injected bundle was null",injectedBundle);
        Assert.assertEquals("The injected bundle was not the bundle1",bundle1,injectedBundle);

        BundleContext injectedContext = bundleProvider.getBundleContext();
        Assert.assertNotNull("The injected bundle context was null",injectedBundle);
        Assert.assertEquals("The injected bundle context was not the bundle1 bundle context",bundle1,injectedContext.getBundle());

        Map<String,String> metadata = bundleProvider.getMetadata();
        Dictionary headers = bundle1.getHeaders();
        Assert.assertNotNull("The injected bundle metadata was null", metadata);
        Assert.assertEquals("The injected bundle metadata had the wrong size",headers.size(),metadata.size());
        for(String s : metadata.keySet()) {
            Assert.assertEquals("The injected metadata header was not the bundle1 header",headers.get(s),metadata.get(s));
        }

        String symbolicName = bundleProvider.getSymbolicName();
        Assert.assertNotNull("The injected bundle symbolic name was null",symbolicName);
        Assert.assertEquals("The injected symbolic name was not the bundle1 symbolic name",bundle1.getSymbolicName(),symbolicName);

        File file = bundleProvider.getFile();
        Assert.assertNotNull("The injected bundle file was null",file);
        Assert.assertEquals("The injected bundle file was not the bundle1 file",injectedContext.getDataFile("test.txt"),file);

        Bundle injectedBundle2 = bundleProvider.getBundle2();
        Assert.assertNotNull("The injected bundle2 was null", injectedBundle2);
        Assert.assertEquals("The injected bundle2 was not the bundle2 proxy",bundle2.getSymbolicName(),injectedBundle2.getSymbolicName());

        BundleContext injectedContext2 = bundleProvider.getBundleContext2();
        Assert.assertNotNull("The injected bundle context2 was null",injectedBundle2);
        Assert.assertEquals("The injected bundle context2 was not the bundle2 bundle context",bundle2,injectedContext2.getBundle());

        Map<String,String> metadata2 = bundleProvider.getMetadata2();
        Dictionary headers2 = bundle2.getHeaders();
        Assert.assertNotNull("The injected bundle metadata2 was null", metadata2);
        Assert.assertEquals("The injected bundle metadata2 had the wrong size",headers2.size(),metadata2.size());
        for(String s : metadata2.keySet()) {
            Assert.assertEquals("The injected metadata2 header was not the bundle2 header",headers2.get(s),metadata2.get(s));
        }

        String symbolicName2 = bundleProvider.getSymbolicName2();
        Assert.assertNotNull("The injected bundle symbolic name2 was null",symbolicName2);
        Assert.assertEquals("The injected symbolic name2 was not the bundle2 symbolic name",bundle2.getSymbolicName(),symbolicName2);

        File file2 = bundleProvider.getFile2();
        Assert.assertNotNull("The injected bundle file2 was null",file2);
        Assert.assertEquals("The injected bundle file2 was not the bundle2 file",injectedContext2.getDataFile("test.txt"),file2);
    }
}
