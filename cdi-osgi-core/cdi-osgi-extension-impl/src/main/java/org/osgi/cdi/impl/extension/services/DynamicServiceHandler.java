package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class DynamicServiceHandler implements InvocationHandler {

    private final Bundle bundle;
    private final String name;
    private Filter filter;

    public DynamicServiceHandler(Bundle bundle, String name) {
        this.bundle = bundle;
        this.name = name;
    }

    public DynamicServiceHandler(Bundle bundle, String name, Filter filter) {
        this.bundle = bundle;
        this.name = name;
        this.filter = filter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        ServiceReference reference = null;
        if (filter != null && filter.value() != null && filter.value().length() > 0) {
            ServiceReference[] refs =
                    bundle.getBundleContext().getServiceReferences(name, filter.value());
            if (refs != null && refs.length > 0) {
                reference = refs[0];
            }
        } else {
            reference = bundle.getBundleContext().getServiceReference(name);
        }
        if (reference == null) {
            throw new IllegalStateException("Can't call service " + name + ". No matching service found.");
        }
        Object instanceToUse = bundle.getBundleContext().getService(reference);
        try {
            return method.invoke(instanceToUse, args);
        } catch(Throwable t) {
            throw new RuntimeException(t);
        } finally {
            bundle.getBundleContext().ungetService(reference);
            CDIOSGiExtension.currentBundle.remove();
        }
    }
}
