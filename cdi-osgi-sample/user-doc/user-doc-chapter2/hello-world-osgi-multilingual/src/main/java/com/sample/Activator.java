package com.sample;

import com.sample.api.HelloWorld;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

    HelloWorld helloWorldEnglish;
    HelloWorld helloWorldFrench;
    HelloWorld helloWorldGerman;

    @Override
    public void start(BundleContext context) throws Exception {
        //say hello when the bundle has initialized

        //first lookup the service using OSGi
        ServiceReference helloWorldEnglishReference = context.getServiceReferences(HelloWorld.class.getName(),"(language.value=ENGLISH)")[0];
        ServiceReference helloWorldFrenchReference = context.getServiceReferences(HelloWorld.class.getName(),"(language.value=FRENCH)")[0];
        ServiceReference helloWorldGermanReference = context.getServiceReferences(HelloWorld.class.getName(),"(language.value=GERMAN)")[0];
        //obtain the service
        helloWorldEnglish = (HelloWorld)context.getService(helloWorldEnglishReference);
        helloWorldFrench = (HelloWorld)context.getService(helloWorldFrenchReference);
        helloWorldGerman = (HelloWorld)context.getService(helloWorldGermanReference);

        helloWorldEnglish.sayHello();
        helloWorldFrench.sayHello();
        helloWorldGerman.sayHello();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        //say goodbye when the bundle has shutdown
        helloWorldEnglish.sayGoodbye();
        helloWorldFrench.sayGoodbye();
        helloWorldGerman.sayGoodbye();
    }
}
