package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import org.jboss.weld.environment.osgi.api.extension.annotation.Filter;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceImpl<T> implements Service<T> {

    private final Class serviceClass;
    private final Class declaringClass;
    private final Bundle bundle;
    private final String serviceName;
    private T service;
    private Filter filter;

    public ServiceImpl(Type t, Bundle bundle) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = null;
        this.bundle = bundle;
    }

    public ServiceImpl(Type t, Bundle bundle, Filter filter) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = null;
        this.bundle = bundle;
        this.filter = filter;
    }

    @Override
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
                            new DynamicServiceHandler(bundle, serviceName, filter));
            }
        } else {
            throw new IllegalStateException("Can't load service from OSGi registry : " + serviceName);
        }
    }

}
