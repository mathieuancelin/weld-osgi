package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Collection;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class ContainerObserver {

    private CDIContainer currentContainer;
    private Collection<CDIContainer> containers;

    public void setCurrentContainer(CDIContainer currentContainer) {
        this.currentContainer = currentContainer;
    }

    public void setContainers(Collection<CDIContainer> containers) {
        this.containers = containers;
    }

    public void listenInterBundleEvents(@Observes InterBundleEvent event) {
        if (!event.isSent()) {
            for (CDIContainer container : containers) {
                if (!container.equals(currentContainer)) {
                    event.sent();
                    try {
                        container.fire(event);
                    } catch (Throwable t) {
                        System.out.println("InterBundle event broadcast failed");
                    }
                }
            }
        }
    }
}
