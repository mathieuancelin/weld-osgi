package org.jboss.weld.environment.osgi.integration;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author mathieu
 */
public class BridgeClassLoader extends ClassLoader {

    private final ClassLoader secondary;

    public BridgeClassLoader(ClassLoader primary, ClassLoader secondary) {
        super(primary);
        this.secondary = secondary;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return secondary.loadClass(name);
    }

    public class BridgeClassLoaderCache {

        private final ClassLoader primary;
        private final Map<ClassLoader, WeakReference<ClassLoader>> cache;

        public BridgeClassLoaderCache(ClassLoader primary) {
            this.primary = primary;
            this.cache = new WeakHashMap<ClassLoader, WeakReference<ClassLoader>>();
        }

        public synchronized ClassLoader resolveBridge(ClassLoader secondary) {
            ClassLoader bridge = null;

            WeakReference<ClassLoader> ref = cache.get(secondary);
            if (ref != null) {
                bridge = ref.get();
            }

            if (bridge == null) {
                bridge = new BridgeClassLoader(primary, secondary);
                cache.put(secondary, new WeakReference<ClassLoader>(bridge));
            }

            return bridge;
        }
    }
}
