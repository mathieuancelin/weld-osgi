/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.cdi.test.jsr299;

import com.sample.osgi.extension.ServiceExtensionProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.*;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class ExtensionTest {

    @Configuration
    public static Option[] configure() {
        return options(
                Environment.CDIOSGiEnvironment(
                        mavenBundle("com.sample.osgi","cdi-osgi-tests-bundle1").version("1.0-SNAPSHOT"),
                        mavenBundle("com.sample.osgi", "cdi-osgi-tests-extension").version("1.0-SNAPSHOT")
                                              )
        );
    }

    @Test
    //@Ignore
    public void eventTest(BundleContext context) throws InterruptedException, InvalidSyntaxException, BundleException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null, bundleExtension = null;
        for(Bundle b : context.getBundles()) {
            Assert.assertEquals("Bundle" + b.getSymbolicName() + " is not ACTIVE", Bundle.ACTIVE, b.getState());
            if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle1")) {
                bundle1=b;
            }
            else if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-extension")) {
                bundleExtension=b;
            }
        }
        Assert.assertNotNull("The bundle1 was not retrieved", bundle1);
        Assert.assertNotNull("The bundleExtension was not retrieved",bundleExtension);

        ServiceReference[] serviceExtensionProviderReferences = context.getServiceReferences(ServiceExtensionProvider.class.getName(),null);
        Assert.assertNotNull("The extension service provider reference array was null",serviceExtensionProviderReferences);
        Assert.assertEquals("The number of extension service provider implementations was wrong", 1,serviceExtensionProviderReferences.length);
        ServiceExtensionProvider provider = (ServiceExtensionProvider)context.getService(serviceExtensionProviderReferences[0]);
        Assert.assertNotNull("The extension service provider was null",provider);

//        PropertyService serviceExtension = provider.getServiceExtension();
//        Assert.assertNotNull("The extension service was null",serviceExtension);
//        Assert.assertEquals("The extension service method result was wrong","Hacked by extension !",serviceExtension.whoAmI());
//        PropertyService serviceExtensionService = provider.getServiceExtensionService();
//        Assert.assertNotNull("The extension service service was null",serviceExtensionService);
//        Assert.assertEquals("The extension service service method result was wrong","Hacked by extension !",serviceExtensionService.whoAmI());
//
//        ServiceReference[] propertyServiceReferences = context.getServiceReferences(PropertyService.class.getName(), null);
//        Assert.assertNotNull("The property service reference array was null",propertyServiceReferences);
//        Assert.assertEquals("The number of property service implementations was wrong", 1,propertyServiceReferences.length);
//        PropertyService propertyService = (PropertyService)context.getService(propertyServiceReferences[0]);
//        Assert.assertNotNull("The extension service provider was null",propertyService);
//        Assert.assertEquals("The property service method result was wrong","Hacked by extension !",propertyService.whoAmI());
    }
}
