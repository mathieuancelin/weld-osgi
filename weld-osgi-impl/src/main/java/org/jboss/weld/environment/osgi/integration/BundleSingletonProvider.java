package org.jboss.weld.environment.osgi.integration;

import java.util.HashMap;
import java.util.Map;
import org.jboss.weld.bootstrap.api.Singleton;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class BundleSingletonProvider extends SingletonProvider {

    public static ThreadLocal<Long> currentBundle =
            new ThreadLocal<Long>();

    @Override
    public <T> Singleton<T> create(Class<? extends T> expectedType) {
        return new BundleSingleton<T>(expectedType);
    }

    private static class BundleSingleton<T> implements Singleton<T> {

        private final Map<Long, T> store = new HashMap<Long, T>();

        private final Class<? extends T> clazz;

        public BundleSingleton(Class<? extends T> clazz) {
            this.clazz = clazz;
        }

        private Long getId() {
            return currentBundle.get();
        }

        @Override
        public T get() {        
            if (!store.containsKey(getId())) {
                T maybeObject = null;
                Throwable t = new Throwable();
                System.out.println("\u001b[1;31mAnalyzing stacktrace : \u001b[m");
                for (StackTraceElement element : t.getStackTrace()) {
                    System.out.println("\u001b[0;31m" + element.getClassName() + "." + element.getMethodName() + "\u001b[m");
                    if (!element.getClassName().startsWith("org.jboss.weld")) {
                        Class<?> maybe = null;
                        try {
                            maybe = this.getClass().getClassLoader().loadClass(element.getClassName());
                        } catch (ClassNotFoundException ex) {
                            //System.out.println("CNFE " + element.getClassName());
                            // Ignore
                        }
                        if (maybe != null) {
                            Bundle maybeBundle = FrameworkUtil.getBundle(maybe);
                            if (maybeBundle != null) {
                                if (!maybeBundle.getSymbolicName().equals("org.jboss.weld.osgi.weld-osgi")) {
                                    currentBundle.set(maybeBundle.getBundleId());
                                    maybeObject = get();
                                    currentBundle.remove();
                                    if (maybeObject != null) {
                                        return maybeObject;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                throw new IllegalStateException("Singleton is not set");
            }
            return store.get(getId());
        }

        @Override
        public void set(T object) {
            store.put(getId(), object);
        }

        @Override
        public void clear() {
            store.remove(getId());
        }

        @Override
        public boolean isSet() {
            return store.containsKey(getId());
        }
    }
}
