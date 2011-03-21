package org.jboss.weld.environment.osgi.integration;

import org.osgi.framework.Bundle;

/**
 *
 * @author mathieu
 */
public class BundleClassLoader extends ClassLoader {

    private final Bundle delegate;

    public BundleClassLoader(Bundle delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("delegating loading : " + name);
        return delegate.loadClass(name);
    }

    @Override
    public String toString() {
        return "BundleClassLoader { delegate = " + delegate.toString() + " }";
    }
}
