package com.sample.calculator.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.osgi.cdi.api.extension.events.BundleContainerInitialized;
import org.osgi.cdi.api.extension.events.BundleContainerShutdown;

@ApplicationScoped
public class App {

    @Inject CalculatorGUI gui;

    public void start(@Observes BundleContainerInitialized init) {
        gui.setVisible(true);
    }

    public void stop(@Observes BundleContainerShutdown init) {
        gui.setVisible(false);
    }
}
