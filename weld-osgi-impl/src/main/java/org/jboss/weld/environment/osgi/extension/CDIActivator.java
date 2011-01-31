package org.jboss.weld.environment.osgi.extension;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import org.jboss.weld.environment.osgi.api.extension.Specification;
import org.jboss.weld.environment.osgi.api.extension.events.AbstractServiceEvent;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceArrival;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceChanged;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceDeparture;
import org.jboss.weld.environment.osgi.integration.BundleSingletonProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

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
                boolean set = BundleSingletonProvider.currentBundle.get() != null;
                BundleSingletonProvider.currentBundle.set(reference.getBundle().getBundleId());
                Event<Object> e = (Event<Object>) context.getService(reference);
                try {
                    e.select(BundleEvent.class).fire(event);
                } catch (Throwable t) {
                    // Ignore
                }
                if (!set)
                    BundleSingletonProvider.currentBundle.remove();
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
                boolean set = BundleSingletonProvider.currentBundle.get() != null;
                BundleSingletonProvider.currentBundle.set(reference.getBundle().getBundleId());
                Event<Object> e = (Event<Object>) context.getService(reference);
                try {
                    e.select(ServiceEvent.class).fire(event);
                } catch (Throwable t) {
                    // Ignore
                }
                ServiceReference ref = event.getServiceReference();
                AbstractServiceEvent serviceEvent = null;
                switch (event.getType()) {
                    case ServiceEvent.MODIFIED:
                        serviceEvent =
                            new ServiceChanged(ref, context);
                        break;
                    case ServiceEvent.REGISTERED:
                        serviceEvent =
                            new ServiceArrival(ref, context);
                        break;
                    case ServiceEvent.UNREGISTERING:
                        serviceEvent =
                            new ServiceDeparture(ref, context);
                        break;
                }
                if (serviceEvent != null) {
                    fireAllEvent(serviceEvent, e);
                }
                if (!set)
                    BundleSingletonProvider.currentBundle.remove();
            }
        }
    }

    private static void fireAllEvent(AbstractServiceEvent event, Event broadcaster) {
        List<Class<?>> classes = event.getServiceClasses();
        Class eventClass = event.getClass();
        for (Class<?> clazz : classes) {
            try {
                broadcaster.select(eventClass,
                    new SpecificationAnnotation(clazz)).fire(event);
            } catch (Throwable t) {
                // Ignore
            }
        }
        //broadcaster.select(eventClass).fire(event);
    }

    private static class SpecificationAnnotation
            extends AnnotationLiteral<Specification>
            implements Specification {

        private final Class value;

        public SpecificationAnnotation(Class value) {
            this.value = value;
        }

        @Override
        public Class value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Specification.class;
        }
    }
}
