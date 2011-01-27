package org.jboss.weld.environment.osgi.extension.context;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.spi.Context;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class BundleContext implements Context {

    private static ConcurrentHashMap<Long, ConcurrentHashMap<Class, StoredBean>> store
            = new ConcurrentHashMap<Long, ConcurrentHashMap<Class, StoredBean>>();

    @Override
    public Class<? extends Annotation> getScope() {
        return BundleScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        Class clazz = bean.getBeanClass();
        Bundle bundle = FrameworkUtil.getBundle(clazz);
        if (bundle == null) {
            throw new RuntimeException("Bundle can't be null");
        }
        if (!store.containsKey(bundle.getBundleId())) {
            store.putIfAbsent(bundle.getBundleId(),
                    new ConcurrentHashMap<Class, StoredBean>());
        }
        ConcurrentHashMap<Class, StoredBean> bundleStore
                = store.get(bundle.getBundleId());
        if (!bundleStore.containsKey(clazz)) {
            StoredBean storedBean = new StoredBean(creationalContext, contextual);
            bundleStore.putIfAbsent(clazz, storedBean);
        }
        return (T) bundleStore.get(clazz);
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        Class clazz = bean.getBeanClass();
        Bundle bundle = FrameworkUtil.getBundle(clazz);
        if (bundle == null) {
            throw new RuntimeException("Bundle can't be null");
        }
        if (!store.containsKey(bundle.getBundleId())) {
            throw new RuntimeException("Can't find bundle store");
        }
        ConcurrentHashMap<Class, StoredBean> bundleStore
                = store.get(bundle.getBundleId());
        if (bundleStore.containsKey(clazz)) {
            throw new RuntimeException("Can't find instance");
        }
        return (T) bundleStore.get(clazz);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public void invalidate() {
        for (ConcurrentHashMap<Class, StoredBean> bundleStore : store.values()) {
            for (StoredBean bean : bundleStore.values()) {
                bean.destroy();
            }
        }
    }

    public static void invalidateBundle(Bundle bundle) {
        ConcurrentHashMap<Class, StoredBean> bundleStore
                = store.get(bundle.getBundleId());
        if (bundleStore != null) {
            for (StoredBean bean : bundleStore.values()) {
                bean.destroy();
            }
        }
    }

    private class StoredBean {
        private final CreationalContext cc;
        private final Contextual contextual;
        private Object instance;

        public StoredBean(CreationalContext cc, Contextual contextual) {
            this.cc = cc;
            this.contextual = contextual;
        }

        public Object getInstance() {
            if (instance == null) {
                instance = contextual.create(cc);
            }
            return instance;
        }

        public Contextual getContextual() {
            return contextual;
        }

        public CreationalContext getCc() {
            return cc;
        }


        public void destroy() {
            cc.release();
            contextual.destroy(instance, cc);
        }
    }
}
