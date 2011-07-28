package com.sample;

import com.sample.api.HelloWorld;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class App {

    @Inject
    HelloWorld helloWorld;

    public void onStartup(@Observes BundleContainerEvents.BundleContainerInitialized event) {
        //say hello when the bundle has initialized
        helloWorld.sayHello();
    }

    public void onShutdown(@Observes BundleContainerEvents.BundleContainerShutdown event) {
        //say goodbye when the bundle has shutdown
        helloWorld.sayGoodbye();
    }
}
