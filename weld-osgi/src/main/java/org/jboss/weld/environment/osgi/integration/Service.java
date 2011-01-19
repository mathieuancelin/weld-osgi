package org.jboss.weld.environment.osgi.integration;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author mathieuancelin
 */
public class Service<T> {

    private final Class serviceClass;
    private final Class declaringClass;
    private final Bundle bundle;
    private final String serviceName;
    private T service;

    public Service(Type t, Class declaring) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = declaring;
        bundle = FrameworkUtil.getBundle(declaringClass);
        if (bundle == null)
            throw new IllegalStateException("Can't have a null bundle.");
    }

    public T get() {
        if (service == null) {
            try {
                populateService();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return service;
    }

    private void populateService() throws Exception {
        ServiceReference ref = bundle.getBundleContext().getServiceReference(serviceName);
        if (ref != null) {
            if (!serviceClass.isInterface()) {
                service = (T) bundle.getBundleContext().getService(ref);
            } else {
                service = (T) Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class[]{(Class) serviceClass},
                            new DynamicServiceHandler(bundle, serviceName));
            }
        } else {
            throw new IllegalStateException("Can't load service from OSGi registry : " + serviceName);
        }
    }

}
