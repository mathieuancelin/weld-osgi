package org.jboss.weld.environment.osgi.extension;

import org.osgi.framework.*;

import javax.enterprise.event.Event;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceArrival;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceChanged;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceDeparture;
import org.jboss.weld.environment.osgi.extension.services.ServiceImpl;

/**
 * It seems we cannot get the BundleContext in the Extension, so
 * to fire up OSGi Events (BundleEvent, ServiceEvent and FrameworkEvent)
 * we need to act here.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class CDIActivator implements BundleActivator,
                                     BundleListener,
                                     ServiceListener {

    private BundleContext context;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        context.addBundleListener(this);
        context.addServiceListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        ServiceReference[] references = findReferences(context, Event.class);

        if (references != null) {
            for (ServiceReference reference : references) {
                Event<Object> e = (Event<Object>) context.getService(reference);
                e.select(BundleEvent.class).fire(event);
            }
        }
    }

    private ServiceReference[] findReferences(BundleContext context, Class<?> type) {
        ServiceReference[] references = null;
        try {
            references = context.getServiceReferences(type.getName(), null);
        } catch (InvalidSyntaxException e) {
            // Ignored
        }
        return references;
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference[] references = findReferences(context, Event.class);

        if (references != null) {
            for (ServiceReference reference : references) {
                Event<Object> e = (Event<Object>) context.getService(reference);
                e.select(ServiceEvent.class).fire(event);
                ServiceReference ref = event.getServiceReference();
                switch (event.getType()) {
                    case ServiceEvent.MODIFIED:
                        ServiceChanged changed = 
                            new ServiceChanged(ref, context);
                        e.select(ServiceChanged.class).fire(changed);
                        break;
                    case ServiceEvent.REGISTERED:
                        ServiceArrival arrival =
                            new ServiceArrival(ref, context);
                        e.select(ServiceArrival.class).fire(arrival);
                        break;
                    case ServiceEvent.UNREGISTERING:
                        ServiceDeparture departure =
                            new ServiceDeparture(ref, context);
                        e.select(ServiceDeparture.class).fire(departure);
                        break;
                }
            }
        }
    }
}
