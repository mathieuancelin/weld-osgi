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
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("loading : " + name);
        Class<?> clazz = null;
        try {
            clazz = getParent().loadClass(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (clazz == null) {
            System.out.println("delegate to weld CL ...");
            return secondary.loadClass(name);
        }
        return clazz;
    }



    @Override
    public String toString() {
        return "BridgeClassLoader { primary = " + super.toString() + ", secondary = " + secondary.toString() + " }";
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
