package org.jboss.weld.environment.osgi.api.extension.events;

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
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public abstract class AbstractServiceEvent {

    public static enum EventType {
        SERVICE_ARRIVAL, SERVICE_DEPARTURE, SERVICE_CHANGED
    }

    private final ServiceReference ref;
    private final BundleContext context;
    private List<String> classesNames;
    private List<Class<?>> classes;
    private Map<Class, Boolean> assignable
            = new HashMap<Class, Boolean>();

    public AbstractServiceEvent(
            ServiceReference ref, BundleContext context) {
        this.ref = ref;
        this.context = context;
    }

    public abstract EventType eventType();

    public ServiceReference getRef() {
        return ref;
    }

    public <T> TypedService<T> type(Class<T> type) {
        if (isTyped(type)) {
            return TypedService.create(type, context, ref);
        } else {
            throw new RuntimeException("the type " + type
                    + " isn't supported for the service. Supported types are "
                    + getServiceClasses());
        }
    }

    public Object getService() {
        return context.getService(ref);
    }

    public boolean ungetService() {
        return context.ungetService(ref);
    }

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

    public Bundle getRegisteringBundle() {
        return ref.getBundle();
    }

    public List<String> getServiceClassNames() {
        if (classesNames == null) {
            classesNames = Arrays.asList((String[])
                    ref.getProperty(Constants.OBJECTCLASS));
        }
        return classesNames;
    }

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
