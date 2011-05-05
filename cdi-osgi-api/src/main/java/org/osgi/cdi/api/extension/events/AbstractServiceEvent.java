package org.osgi.cdi.api.extension.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * <p>Represents all the CDI-OSGi service events as a superclass.</p> <p/> <p>Provides a way to listen all service
 * events in a single method. Allows to retrieve both original event type as a {@link BundleEventType} and the firing
 * service informations.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see ServiceEventType
 */
public abstract class AbstractServiceEvent {

    private final ServiceReference reference;
    private final BundleContext context;
    private List<String> classesNames;
    private List<Class<?>> classes;
    private Map<Class, Boolean> assignable
            = new HashMap<Class, Boolean>();

    /**
     * Construct a new service event for the specified service.
     *
     * @param reference the {@link ServiceReference} that changes of state.
     * @param context   the service {@link BundleContext}.
     */
    public AbstractServiceEvent(
            ServiceReference reference, BundleContext context) {
        this.reference = reference;
        this.context = context;
    }

    /**
     * Get the service event type.
     *
     * @return the {@link ServiceEventType} of the fired service event.
     */
    public abstract ServiceEventType eventType();

    /**
     * Get the service reference of the firing service.
     *
     * @return the {@link ServiceReference} of the fired service event.
     */
    public ServiceReference getReference() {
        return reference;
    }

    /**
     * TODO
     *
     * @param type
     * @param <T>
     * @return
     */
    public <T> TypedService<T> type(Class<T> type) {
        if (isTyped(type)) {
            return TypedService.create(type, context, reference);
        } else {
            throw new RuntimeException("the type " + type
                                       + " isn't supported for the service. Supported types are "
                                       + getServiceClasses());
        }
    }

    /**
     * Get a service instance of the firing service.
     *
     * @return the service instance of the firing service.
     * @see BundleContext#getService(org.osgi.framework.ServiceReference)
     */
    public Object getService() {
        return context.getService(reference);
    }

    /**
     * Release the service instance of the firing service.
     *
     * @return false if the service instance is already released or if the service is unavailable, true otherwhise.
     * @see BundleContext#ungetService(org.osgi.framework.ServiceReference)
     */
    public boolean ungetService() {
        return context.ungetService(reference);
    }

    /**
     * If the specified type is a implementation of the firing service.
     *
     * @param type the tested type for being a firing service implementation.
     * @return true if the specified type is assignable for the firing service, false otherwise.
     */
    public boolean isTyped(Class<?> type) {
        boolean typed = false;
        if (!assignable.containsKey(type)) {
            for (Class clazz : getServiceClasses()) {
                if (type.isAssignableFrom(clazz)) {
                    typed = true;
                    break;
                }
            }
            assignable.put(type, typed);
        }
        return assignable.get(type);
    }

    /**
     * Get the registering bundle of the firing service.
     *
     * @return the registering bundle.
     */
    public Bundle getRegisteringBundle() {
        return reference.getBundle();
    }

    /**
     * Get the class names under which the firing service was registered.
     *
     * @return all the class names for the firing service.
     */
    public List<String> getServiceClassNames() {
        if (classesNames == null) {
            classesNames = Arrays.asList((String[])
                                                 reference.getProperty(Constants.OBJECTCLASS));
        }
        return classesNames;
    }

    /**
     * Get the class that are the firing service implementations.
     *
     * @return all the firing service implementation classes.
     */
    public List<Class<?>> getServiceClasses() {
        if (classes == null) {
            classes = new ArrayList<Class<?>>();
            for (String className : getServiceClassNames()) {
                try {
                    classes.add(getClass()
                                        .getClassLoader().loadClass(className));
                } catch (ClassNotFoundException ex) {
                    return null;
                }
            }
        }
        return classes;
    }

    /**
     * TODO
     *
     * @param <T>
     */
    public static class TypedService<T> {

        private final BundleContext context;
        private final ServiceReference ref;
        private final Class<T> type;

        TypedService(BundleContext context,
                     ServiceReference ref, Class<T> type) {
            this.context = context;
            this.ref = ref;
            this.type = type;
        }

        static <T> TypedService<T> create(Class<T> type
                , BundleContext context, ServiceReference ref) {
            return new TypedService<T>(context, ref, type);
        }

        public T getService() {
            return type.cast(context.getService(ref));
        }

        public boolean ungetService() {
            return context.ungetService(ref);
        }
    }
}