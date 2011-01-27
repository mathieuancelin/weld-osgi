/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.environment.osgi.extension.services;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class BundleContext implements Context {

    private static ConcurrentHashMap<Long, ConcurrentHashMap<Class, Object>> store
            = new ConcurrentHashMap<Long, ConcurrentHashMap<Class, Object>>();

    @Override
    public Class<? extends Annotation> getScope() {
        return BundleScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        T instance = null;
        Bean bean = (Bean) contextual;
        Class clazz = bean.getBeanClass();
        Bundle bundle = FrameworkUtil.getBundle(clazz);
        if (bundle == null) {
            throw new RuntimeException("Bundle can't be null");
        }
        if (!store.containsKey(bundle.getBundleId())) {
            store.putIfAbsent(bundle.getBundleId(),
                    new ConcurrentHashMap<Class, Object>());
        }
        ConcurrentHashMap<Class, Object> bundleStore
                = store.get(bundle.getBundleId());
        if (!bundleStore.containsKey(clazz)) {
            instance = contextual.create(creationalContext);
            bundleStore.putIfAbsent(clazz, instance);
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
        ConcurrentHashMap<Class, Object> bundleStore
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
}
