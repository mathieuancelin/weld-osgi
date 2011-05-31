package com.sample.calculator.core;

import org.osgi.cdi.api.extension.events.BundleContainerEvents;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class App {

    @Inject CalculatorGUI gui;

    public void start(@Observes BundleContainerEvents.BundleContainerInitialized init) {
        gui.setVisible(true);
    }

    public void stop(@Observes BundleContainerEvents.BundleContainerShutdown init) {
        gui.setVisible(false);
    }
}
