package org.jboss.weld.environment.osgi.api.extension.events;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author mathieu
 */
public abstract class AbstractServiceEvent {

    private final ServiceReference ref;
    private final BundleContext context;

    public AbstractServiceEvent(
            ServiceReference ref, BundleContext context) {
        this.ref = ref;
        this.context = context;
    }

    public ServiceReference getRef() {
        return ref;
    }

    public <T> TypedService<T> type(Class<T> type) {
        return TypedService.create(type, context, ref);
    }

    public Object getService() {
        return context.getService(ref);
    }

    public boolean ungetService() {
        return context.ungetService(ref);
    }

    public boolean isTyped(Class<?> type) {
        return type.isAssignableFrom(getServiceClass());
    }

    public Bundle getRegisteringBundle() {
        return ref.getBundle();
    }

    public String getServiceClassName() {
        return ((String[]) ref.getProperty(Constants.OBJECTCLASS))[0];
    }

    public Class<?> getServiceClass() {
        try {
            return getClass().getClassLoader().loadClass(getServiceClassName());
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public static class TypedService<T> {

        private final BundleContext context;
        private final ServiceReference ref;
        private final Class<T> type;

       TypedService(BundleContext context, ServiceReference ref, Class<T> type) {
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
