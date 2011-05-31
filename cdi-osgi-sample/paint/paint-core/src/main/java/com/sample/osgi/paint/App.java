package com.sample.osgi.paint;

import com.sample.osgi.paint.gui.PaintFrame;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;
import org.osgi.cdi.api.extension.events.Invalid;
import org.osgi.cdi.api.extension.events.Valid;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class App {

    @Inject PaintFrame frame;

    public void onStartup(@Observes BundleContainerEvents.BundleContainerInitialized event) {
        System.out.println("CDI Container for bundle "
                + event.getBundleContext().getBundle() + " started");
        frame.start();
    }

    public void onShutdown(@Observes BundleContainerEvents.BundleContainerShutdown event) {
        frame.stop();
    }

    public void validListen(@Observes Valid valid) {
        frame.start();
    }

    public void invalidListen(@Observes Invalid valid) {
        frame.stop();
    }
}
