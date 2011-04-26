package org.jboss.weld.environment.osgi.integration;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.jboss.weld.exceptions.WeldException;
import org.jboss.weld.logging.messages.BeanMessage;
import org.jboss.weld.serialization.spi.ProxyServices;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiProxyService implements ProxyServices {
    
    private final ClassLoader loader;

    public OSGiProxyService() {
        this.loader = getClass().getClassLoader();
    }

    @Override
    public ClassLoader getClassLoader(Class<?> proxiedBeanType) {
        return new BridgeClassLoader(proxiedBeanType.getClassLoader(), loader);
    }

    @Override
    public Class<?> loadBeanClass(final String className) {
        try {
            return (Class<?>) AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {

                public Object run() throws Exception {
                    return Class.forName(className, true, getClassLoader(this.getClass()));
                }
            });
        } catch (PrivilegedActionException pae) {
            throw new WeldException(BeanMessage.CANNOT_LOAD_CLASS, className, pae.getException());
        }
    }

    @Override
    public void cleanup() {
        // no cleanup
    }

    private static class BridgeClassLoader extends ClassLoader {

        private final ClassLoader delegate;
        private final ClassLoader infra;

        public BridgeClassLoader(ClassLoader delegate, ClassLoader infraClassLoader) {
            this.delegate = delegate;
            this.infra = infraClassLoader;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            Class<?> loadedClass = null;
            try {
                loadedClass = delegate.loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                // todo : filter on utils class only
                loadedClass = infra.loadClass(name);
            }
            return loadedClass;
        }
    }
}
