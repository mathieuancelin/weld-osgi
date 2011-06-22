/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.*;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.events.AbstractBundleEvent;
import org.osgi.cdi.api.extension.events.AbstractServiceEvent;
import org.osgi.cdi.api.extension.events.BundleEvents;
import org.osgi.cdi.api.extension.events.ServiceEvents;
import org.osgi.framework.*;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the activator of the CDI-OSGi extension part. It starts with the extension bundle.
 * <p/>
 * It seems we cannot get the BundleContext in the Extension, so to fire up OSGi Events (BundleEvent, ServiceEvent and
 * FrameworkEvent) we need to act here.
 * Thus it listens to all {@link BundleEvent}s and {@link ServiceEvent}s in order to relay them using {@link Event}s.
 *
 * @author Guillaume Sauthier
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class ExtensionActivator implements BundleActivator, BundleListener, ServiceListener {

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

        if (references != null) { //if there are some listening bean bundles
            Bundle bundle = event.getBundle();
            AbstractBundleEvent bundleEvent = null;
            switch (event.getType()) {
                case BundleEvent.INSTALLED:
                    bundleEvent = new BundleEvents.BundleInstalled(bundle);
                    break;
                case BundleEvent.LAZY_ACTIVATION:
                    bundleEvent = new BundleEvents.BundleLazyActivation(bundle);
                    break;
                case BundleEvent.RESOLVED:
                    bundleEvent = new BundleEvents.BundleResolved(bundle);
                    break;
                case BundleEvent.STARTED:
                    bundleEvent = new BundleEvents.BundleStarted(bundle);
                    break;
                case BundleEvent.STARTING:
                    bundleEvent = new BundleEvents.BundleStarting(bundle);
                    break;
                case BundleEvent.STOPPED:
                    bundleEvent = new BundleEvents.BundleStopped(bundle);
                    break;
                case BundleEvent.STOPPING:
                    bundleEvent = new BundleEvents.BundleStopping(bundle);
                    break;
                case BundleEvent.UNINSTALLED:
                    bundleEvent = new BundleEvents.BundleUninstalled(bundle);
                    break;
                case BundleEvent.UNRESOLVED:
                    bundleEvent = new BundleEvents.BundleUnresolved(bundle);
                    break;
                case BundleEvent.UPDATED:
                    bundleEvent = new BundleEvents.BundleUpdated(bundle);
                    break;
            }
            for (ServiceReference reference : references) { //broadcast event
                boolean set = CDIOSGiExtension.currentBundle.get() != null;
                CDIOSGiExtension.currentBundle.set(reference.getBundle().getBundleId());
                Event<Object> e = (Event<Object>) context.getService(reference);
                try {
                    //broadcast the OSGi event through CDI event system
                    e.select(BundleEvent.class).fire(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                if (bundleEvent != null) {
                    //broadcast the corresponding CDI-OSGi event
                    fireAllEvent(bundleEvent, e);
                }
                if (!set) {
                    CDIOSGiExtension.currentBundle.remove();
                }
            }
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference[] references = findReferences(context, Instance.class);

        if (references != null) { //if there are some listening bean bundles
            ServiceReference ref = event.getServiceReference();
            AbstractServiceEvent serviceEvent = null;
            switch (event.getType()) {
                case ServiceEvent.MODIFIED:
                    serviceEvent =
                            new ServiceEvents.ServiceChanged(ref, context);
                    break;
                case ServiceEvent.REGISTERED:
                    serviceEvent =
                            new ServiceEvents.ServiceArrival(ref, context);
                    break;
                case ServiceEvent.UNREGISTERING:
                    serviceEvent =
                            new ServiceEvents.ServiceDeparture(ref, context);
                    break;
            }
            for (ServiceReference reference : references) { //broadcast event
                boolean set = CDIOSGiExtension.currentBundle.get() != null;
                CDIOSGiExtension.currentBundle.set(reference.getBundle().getBundleId());
                Instance<Object> instance = (Instance<Object>) context.getService(reference);
                Event<Object> e = instance.select(Event.class).get();
                try {
                    //broadcast the OSGi event through CDI event system
                    e.select(ServiceEvent.class).fire(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                if (serviceEvent != null) {
                    //broadcast the corresponding CDI-OSGi event
                    fireAllEvent(serviceEvent, e, instance);
                }
                if (!set) {
                    CDIOSGiExtension.currentBundle.remove();
                }
            }
        }
    }

    private ServiceReference[] findReferences(BundleContext context, Class<?> type) {
        ServiceReference[] references = null;
        try {
            references = context.getServiceReferences(type.getName(), null);
        } catch (InvalidSyntaxException e) {// Ignored
        }
        return references;
    }

    private void fireAllEvent(AbstractServiceEvent event, Event broadcaster, Instance<Object> instance) {
        List<Class<?>> classes = event.getServiceClasses();
        Class eventClass = event.getClass();
        for (Class<?> clazz : classes) {
            try {
                broadcaster.select(eventClass,
                                   filteredServicesQualifiers(event,
                                                              new SpecificationAnnotation(clazz),
                                                              instance))
                           .fire(event);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private Annotation[] filteredServicesQualifiers(AbstractServiceEvent event,
                                                    SpecificationAnnotation specific,
                                                    Instance<Object> instance) {
        Set<Annotation> eventQualifiers = new HashSet<Annotation>();
        eventQualifiers.add(specific);
        CDIOSGiExtension extension = instance.select(CDIOSGiExtension.class).get();
        for (Annotation annotation : extension.getObservers()) {
            String value = ((Filter) annotation).value();
            try {
                org.osgi.framework.Filter filter
                        = context.createFilter(value);
                if (filter.match(event.getReference())) {
                    eventQualifiers.add(new FilterAnnotation(value));
                }
            } catch (InvalidSyntaxException ex) {
                //ex.printStackTrace();
            }
        }
        return eventQualifiers.toArray(new Annotation[eventQualifiers.size()]);
    }

    private void fireAllEvent(AbstractBundleEvent event, Event broadcaster) {
        try {
            broadcaster.select(event.getClass(),
                               new BundleNameAnnotation(event.getSymbolicName()),
                               new BundleVersionAnnotation(event.getVersion().toString()))
                       .fire(event);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static class BundleNameAnnotation extends AnnotationLiteral<BundleName> implements BundleName {

        private final String value;

        public BundleNameAnnotation(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return BundleName.class;
        }
    }

    private static class BundleVersionAnnotation extends AnnotationLiteral<BundleVersion> implements BundleVersion {

        private final String value;

        public BundleVersionAnnotation(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return BundleVersion.class;
        }
    }

    public static class SpecificationAnnotation extends AnnotationLiteral<Specification> implements Specification {

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

    public static class SentAnnotation extends AnnotationLiteral<Sent> implements Sent {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Sent.class;
        }
    }

    public static class FilterAnnotation extends AnnotationLiteral<Filter> implements Filter {

        private final String value;

        public FilterAnnotation(String value) {
            this.value = value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Filter.class;
        }

        @Override
        public String value() {
            return value;
        }
    }
}
