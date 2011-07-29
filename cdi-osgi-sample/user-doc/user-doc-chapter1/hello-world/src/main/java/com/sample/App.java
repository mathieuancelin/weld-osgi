package com.sample;

import com.sample.api.HelloWorld;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class App {

    @Inject
    HelloWorld helloWorld;

    public void onStartup(@Observes BundleContainerEvents.BundleContainerInitialized event) {
        helloWorld.sayHello();
    }

    public void onShutdown(@Observes BundleContainerEvents.BundleContainerShutdown event) {
        helloWorld.sayGoodbye();
    }
}
