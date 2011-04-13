package org.jboss.weld.environment.osgi.extension.services;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import org.jboss.weld.environment.osgi.api.extension.BundleContainer;
import org.jboss.weld.environment.osgi.api.extension.BundleContainers;
import org.jboss.weld.environment.osgi.api.extension.events.AbstractBundleEvent;
import org.jboss.weld.environment.osgi.api.extension.events.AbstractServiceEvent;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerShutdown;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class ContainerObserver {

    private List<Class<?>> excludes = new ArrayList<Class<?>>();
    private BundleContainer currentContainer;
    private BundleContainers containers;

    public ContainerObserver() {
        excludes.add(BundleContainerInitialized.class);
        excludes.add(BundleContainerShutdown.class);
    }

    public void setCurrentContainer(BundleContainer currentContainer) {
        this.currentContainer = currentContainer;
    }

    public void setContainers(BundleContainers containers) {
        this.containers = containers;
    }

    public void listen(@Observes Object event) {
        Class<?> eventClass = event.getClass();
        if (AbstractBundleEvent.class.isAssignableFrom(eventClass)
                && AbstractServiceEvent.class.isAssignableFrom(eventClass)
                && !excludes.contains(eventClass)) {
            for (BundleContainer container : containers.getContainers()) {
                if (!container.equals(currentContainer)) {
                    container.fire(event);
                }
            }
        }
    }
}
