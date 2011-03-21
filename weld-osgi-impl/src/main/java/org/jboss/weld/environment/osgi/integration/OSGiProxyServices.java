package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.serialization.spi.ProxyServices;
import org.osgi.framework.Bundle;

/**
 *
 * @author mathieu
 */
public class OSGiProxyServices implements ProxyServices {

    private final Bundle bundle;

    public OSGiProxyServices(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public ClassLoader getClassLoader(Class<?> proxiedBeanType) {
        System.out.println("getclassloader " + proxiedBeanType.getName() + " .......................................");

        return getClass().getClassLoader();
    }

    @Override
    public Class<?> loadBeanClass(String className) {
        System.out.println("loadBeanClass " + className + " .......................................");
        try {
            return bundle.loadClass(className);
        } catch (ClassNotFoundException ex) {
           throw new RuntimeException(ex);
        }
    }

    @Override
    public void cleanup() {
        // nothing
    }
}
