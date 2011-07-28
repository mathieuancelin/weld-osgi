package com.sample;

import com.sample.api.HelloWorld;
import com.sample.api.Language;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class App {

    @Inject @Language()
    HelloWorld helloWorldEnglish;

    @Inject @Language("FRENCH")
    HelloWorld helloWorldFrench;

    @Inject @Language("GERMAN")
    HelloWorld helloWorldGerman;

    public void onStartup(@Observes BundleContainerEvents.BundleContainerInitialized event) {
        //say hello when the bundle has initialized
        helloWorldEnglish.sayHello();
        helloWorldFrench.sayHello();
        helloWorldGerman.sayHello();
    }

    public void onShutdown(@Observes BundleContainerEvents.BundleContainerShutdown event) {
        //say goodbye when the bundle has shutdown
        helloWorldEnglish.sayGoodbye();
        helloWorldFrench.sayGoodbye();
        helloWorldGerman.sayGoodbye();
    }
}
