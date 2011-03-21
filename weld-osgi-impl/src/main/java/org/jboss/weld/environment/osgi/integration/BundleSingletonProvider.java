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

        private static Map<String, Bundle> classes = new HashMap<String, Bundle>();

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
                for (StackTraceElement element : t.getStackTrace()) {
                    String className = element.getClassName();
                    if (!className.startsWith("org.jboss.weld") 
                            && !className.startsWith("java")
                            && !className.startsWith("org.apache.felix.framework")) {
                        
                        if(!classes.containsKey(className)) {
                            System.out.println("\u001b[1;31mAnalyzing stacktrace : \u001b[m");
                            System.out.println("\u001b[0;31m" + className + "." + element.getMethodName() + "\u001b[m");
                            Class<?> maybe = null;
                            try {
                                maybe = this.getClass().getClassLoader().loadClass(className);
                            } catch (ClassNotFoundException ex) {
                                //System.out.println("CNFE " + element.getClassName());
                                // Ignore
                            }
                            if (maybe != null) {
                                Bundle bundle = FrameworkUtil.getBundle(maybe);
                                if (bundle != null) {
                                    classes.put(className, bundle);
                                }
                            }
                        }
                        Bundle maybeBundle = classes.get(className);
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
