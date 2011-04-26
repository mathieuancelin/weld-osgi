package org.jboss.weld.environment.osgi.api.extension.events;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceChanged extends AbstractServiceEvent {

     public ServiceChanged(
            ServiceReference ref, BundleContext context) {
        super(ref, context);
    }

    @Override
    public EventType eventType() {
        return EventType.SERVICE_CHANGED;
    }
}