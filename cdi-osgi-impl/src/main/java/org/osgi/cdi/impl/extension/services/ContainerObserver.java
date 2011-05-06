package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class ContainerObserver {

    private CDIContainer currentContainer;
    private CDIContainers containers;

    public void setCurrentContainer(CDIContainer currentContainer) {
        this.currentContainer = currentContainer;
    }

    public void setContainers(CDIContainers containers) {
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
                        System.out.println("InterBundle event broadcast");
                    }
                }
            }
        }
    }
}
