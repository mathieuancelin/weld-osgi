package org.osgi.cdi.impl.extension.services;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceReferenceHandler implements InvocationHandler {

    private final ServiceReference ref;
    private final BundleContext registry;

    public ServiceReferenceHandler(ServiceReference ref, BundleContext registry) {
        this.ref = ref;
        this.registry = registry;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object instanceToUse = registry.getService(ref);
        try {
            return method.invoke(instanceToUse, args);
        } finally {
            registry.ungetService(ref);
        }
    }
}
