package org.jboss.weld.environment.osgi.extension.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.util.TypeLiteral;
import org.jboss.weld.environment.osgi.api.extension.annotation.Filter;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceImpl<T> implements Service<T> {

    private final Class serviceClass;
    private final BundleContext registry;
    private final String serviceName;
    private List<T> services = new ArrayList<T>();
    private T service;
    private Filter filter;

    public ServiceImpl(Type t, BundleContext registry) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        this.registry = registry;
    }

    public ServiceImpl(Type t, BundleContext registry, Filter filter) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        this.registry = registry;
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
        ServiceReference ref = registry.getServiceReference(serviceName);
        if (ref != null) {
            if (!serviceClass.isInterface()) {
                service = (T) registry.getService(ref);
            } else {
                service = (T) Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class[]{(Class) serviceClass},
                            new DynamicServiceHandler(registry.getBundle(), serviceName, filter));
            }
        } else {
            throw new IllegalStateException("Can't load service from OSGi registry : " + serviceName);
        }
    }

    private void populateServices() throws Exception {
        services.clear();
        String filterString = null;
        if (filter != null && !filter.value().equals("")) {
            filterString = filter.value();
        }
        ServiceReference[] refs = registry.getServiceReferences(serviceName, filterString);
        if (refs != null) {
            for (ServiceReference ref : refs) {
                if (!serviceClass.isInterface()) {
                    services.add((T) registry.getService(ref));
                } else {
                    services.add((T) Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class[]{(Class) serviceClass},
                            new ServiceReferenceHandler(ref, registry)));
                }
            }
        }
    }

    @Override
    public Service<T> select(Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <U extends T> Service<U> select(Class<U> subtype, Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <U extends T> Service<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isUnsatisfied() {
        return (size() <= 0);
    }

    @Override
    public boolean isAmbiguous() {
        return (size() > 1);
    }

    @Override
    public Iterator<T> iterator() {
        try {
            populateServices();
        } catch (Exception ex) {
            ex.printStackTrace();
            services = Collections.emptyList();
        }
        return services.iterator();
    }

    @Override
    public int size() {
        try {
            ServiceReference[] refs = registry.getServiceReferences(serviceName, null);
            if (refs == null) {
                return 0;
            } else {
                return refs.length;
            }
        } catch (InvalidSyntaxException ex) {
            return -1;
        }
    }
}
