package com.sample.osgi.paint;

import com.sample.osgi.paint.gui.PaintFrame;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerInitialized;

@ApplicationScoped
public class App {

    @Inject PaintFrame frame;

    @PostConstruct
    public void start() {
        frame.start();
    }

    @PreDestroy
    public void stop() {
        frame.stop();
    }

    public void onStartup(@Observes BundleContainerInitialized event) {
        System.out.println("CDI Container for bundle "
                + event.getBundleContext().getBundle() + " started");
    }
}
