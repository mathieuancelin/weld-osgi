package org.jboss.weld.environment.osgi.api.extension.events;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author mathieu
 */
public class ServiceDeparture extends AbstractServiceEvent {

     public ServiceDeparture(
            ServiceReference ref, BundleContext context) {
        super(ref, context);
    }
}