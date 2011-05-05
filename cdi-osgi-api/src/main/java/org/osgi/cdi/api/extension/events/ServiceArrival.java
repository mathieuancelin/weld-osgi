package org.osgi.cdi.api.extension.events;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceArrival extends AbstractServiceEvent {

     public ServiceArrival(
            ServiceReference ref, BundleContext context) {
        super(ref, context);
    }

    @Override
    public ServiceEventType eventType() {
        return ServiceEventType.SERVICE_ARRIVAL;
    }
}
