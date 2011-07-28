package com.sample;

import com.sample.api.HelloWorld;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

    HelloWorld helloWorld;

    @Override
    public void start(BundleContext context) throws Exception {
        //say hello when the bundle has initialized

        //first lookup the service using OSGi
        ServiceReference helloWorldReference = context.getServiceReference(HelloWorld.class.getName());
        //obtain the service
        helloWorld = (HelloWorld)context.getService(helloWorldReference);

        helloWorld.sayHello();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        //say goodbye when the bundle has shutdown
        helloWorld.sayGoodbye();
    }
}
