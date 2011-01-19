package org.jboss.weld.environment.osgi.integration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author mathieu
 */
public class DynamicServiceHandler implements InvocationHandler {

    private final Bundle bundle;
    private final String name;

    public DynamicServiceHandler(Bundle bundle, String name) {
        this.bundle = bundle;
        this.name = name;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("dynamic call !!!");
        ServiceReference reference = bundle.getBundleContext().getServiceReference(name);
        Object instanceToUse = bundle.getBundleContext().getService(reference);
        try {
            return method.invoke(instanceToUse, args);
        } finally {
            bundle.getBundleContext().ungetService(reference);
        }
    }

}
