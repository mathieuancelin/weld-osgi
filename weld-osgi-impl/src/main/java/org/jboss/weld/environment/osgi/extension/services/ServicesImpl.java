package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jboss.weld.environment.osgi.api.extension.Services;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServicesImpl<T> implements Services<T> {

    private final Class serviceClass;
    private final Class declaringClass;
    private final String serviceName;
    private final BundleContext registry;
    private List<T> services = new ArrayList<T>();

    public ServicesImpl(Type t, Class declaring, BundleContext registry) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = declaring;
        this.registry = registry;
    }

    @Override
    public Iterator<T> iterator() {
        try {
            populateServiceRef();
        } catch (Exception ex) {
            ex.printStackTrace();
            services = Collections.emptyList();
        }
        return services.iterator();
    }

    private void populateServiceRef() throws Exception {
        services.clear();

        ServiceReference[] refs = registry.getServiceReferences(serviceName, null);
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
        } else {
            services = Collections.emptyList();
        }
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
