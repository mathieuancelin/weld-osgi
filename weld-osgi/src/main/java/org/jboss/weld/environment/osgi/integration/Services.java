package org.jboss.weld.environment.osgi.integration;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author mathieuancelin
 */
public class Services<T> implements Iterable<T> {

    private final Class serviceClass;
    private final Class declaringClass;
    private final Bundle bundle;
    private final String serviceName;

    private List<T> services = new ArrayList<T>();

    public Services(Type t, Class declaring) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = declaring;
        bundle = FrameworkUtil.getBundle(declaringClass);
        if (bundle == null)
            throw new IllegalStateException("Can't have a null bundle.");
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
        ServiceReference[] refs = bundle.getBundleContext().getServiceReferences(serviceName, null);
        if (refs != null) {
            for (ServiceReference ref : refs) {
                if (!serviceClass.isInterface()) {
                    services.add((T) bundle.getBundleContext().getService(ref));
                } else {
                    services.add((T) Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class[]{(Class) serviceClass},
                                new DynamicServiceHandler(bundle, serviceName)));
                }
            }
        } else {
            services = Collections.emptyList();
        }
    }
}
