package org.jboss.weld.environment.osgi.api.extension.events;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author mathieu
 */
public class ServiceDeparture {

    private final ServiceReference ref;
    private final Object service;

    public ServiceDeparture(
            ServiceReference ref, Object service) {
        this.ref = ref;
        this.service = service;
    }

    public ServiceReference getRef() {
        return ref;
    }

    public Object getService() {
        return service;
    }

    public Bundle getRegisteringBundle() {
        return ref.getBundle();
    }
}
