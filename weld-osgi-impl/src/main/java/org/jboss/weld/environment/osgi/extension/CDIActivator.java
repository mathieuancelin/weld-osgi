package org.jboss.weld.environment.osgi.extension;

import org.osgi.framework.*;

import javax.enterprise.event.Event;

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

    @Override
    public void start(BundleContext context) throws Exception {
        context.addBundleListener(this);
        context.addServiceListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        BundleContext context = event.getBundle().getBundleContext();
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
        } catch (NullPointerException e) {
            // Ignored
        }
        return references;
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        BundleContext context = event.getServiceReference().getBundle().getBundleContext();
        ServiceReference[] references = findReferences(context, Event.class);

        if (references != null) {
            for (ServiceReference reference : references) {
                Event<Object> e = (Event<Object>) context.getService(reference);
                e.select(ServiceEvent.class).fire(event);
            }
        }
    }
}
