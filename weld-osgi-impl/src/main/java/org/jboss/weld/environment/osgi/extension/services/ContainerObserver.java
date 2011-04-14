package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.jboss.weld.environment.osgi.api.extension.BundleContainer;
import org.jboss.weld.environment.osgi.api.extension.BundleContainers;
import org.jboss.weld.environment.osgi.api.extension.events.InterBundleEvent;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class ContainerObserver {

    private BundleContainer currentContainer;
    private BundleContainers containers;

    public void setCurrentContainer(BundleContainer currentContainer) {
        this.currentContainer = currentContainer;
    }

    public void setContainers(BundleContainers containers) {
        this.containers = containers;
    }

    public void listenInterBundleEvents(@Observes InterBundleEvent event) {
        if (!event.isSent()) {
            for (BundleContainer container : containers.getContainers()) {
                if (!container.equals(currentContainer)) {
                    event.setSent(true);
                    container.fire(event);
                }
            }
        }
    }
}
