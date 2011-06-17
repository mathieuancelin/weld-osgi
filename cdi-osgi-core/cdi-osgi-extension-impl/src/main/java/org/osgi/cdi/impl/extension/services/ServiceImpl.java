package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.impl.extension.FilterGenerator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceImpl<T> implements Service<T> {

    private final Class serviceClass;
    private final BundleContext registry;
    private final String serviceName;

    private List<T> services = new ArrayList<T>();
    private T service = null;
    private Filter filter;

    public ServiceImpl(Type t, BundleContext registry) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        this.registry = registry;
        filter = FilterGenerator.makeFilter();
    }

    public ServiceImpl(Type t, BundleContext registry, Filter filter) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        this.registry = registry;
        this.filter = filter;
    }

    @Override
    public T get() {
        if(service == null) {
            try {
                populateServices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return service;
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
        service = services.size() > 0 ? services.get(0) : null;
    }

    @Override
    public Service<T> select(Annotation... qualifiers) {
        service = null;
        filter = FilterGenerator.makeFilter(filter, Arrays.asList(qualifiers));
        return this;
    }

    @Override
    public Service<T> select(String filter) {
        service = null;
        this.filter = FilterGenerator.makeFilter(this.filter,filter);
        return this;
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
    public int size() {
        if(service == null) {
            try {
                populateServices();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        return services.size();
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
}
