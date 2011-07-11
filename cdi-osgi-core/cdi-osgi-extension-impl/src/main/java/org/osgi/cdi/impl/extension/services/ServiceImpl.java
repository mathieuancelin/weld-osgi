/**
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
package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.impl.extension.FilterGenerator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Implementation of {@link Service}.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class ServiceImpl<T> implements Service<T> {

    private static Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

    private final Class serviceClass;
    private final BundleContext registry;
    private final String serviceName;

    private List<T> services = new ArrayList<T>();
    private T service = null;
    private Filter filter;

    public ServiceImpl(Type t, BundleContext registry) {
        logger.debug("Creation of a new service provider for bundle {} as {} with no filter", registry.getBundle(), t);
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        this.registry = registry;
        filter = FilterGenerator.makeFilter();
    }

    public ServiceImpl(Type t, BundleContext registry, Filter filter) {
        logger.debug("Creation of a new service provider for bundle {} as {} with filter {}", new Object[] {registry.getBundle(), t, filter});
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        this.registry = registry;
        this.filter = filter;
    }

    @Override
    public T get() {
        if(service == null) {
            try {
                populateServices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return service;
    }

    private void populateServices() throws Exception {
        logger.trace("Scanning matching service for service provider {}", this);
        services.clear();
        String filterString = null;
        if (filter != null && !filter.value().equals("")) {
            filterString = filter.value();
        }
        ServiceTracker tracker = new ServiceTracker(registry, registry.createFilter(
                "(&(objectClass=" + serviceName + ")" + filterString + ")"), null);
        tracker.open();
//        ServiceReference[] refs = registry.getServiceReferences(serviceName, filterString);
        Object[] instances = tracker.getServices();
        if (instances != null) {
            for (Object ref : instances) {
                services.add((T) ref);
//                if (!serviceClass.isInterface()) {
//                    services.add((T) registry.getService(ref));
//                } else {
//                    services.add((T) Proxy.newProxyInstance(
//                            getClass().getClassLoader(),
//                            new Class[]{(Class) serviceClass},
//                            new ServiceReferenceHandler(ref, registry)));
//                }
            }
        }
        service = services.size() > 0 ? services.get(0) : null;
    }

    @Override
    public Service<T> select(Annotation... qualifiers) {
        service = null;
        filter = FilterGenerator.makeFilter(filter, Arrays.asList(qualifiers));
        return this;
    }

    @Override
    public Service<T> select(String filter) {
        service = null;
        this.filter = FilterGenerator.makeFilter(this.filter,filter);
        return this;
    }

    @Override
    public boolean isUnsatisfied() {
        return (size() <= 0);
    }

    @Override
    public boolean isAmbiguous() {
        return (size() > 1);
    }

    @Override
    public int size() {
        if(service == null) {
            try {
                populateServices();
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        return services.size();
    }

    @Override
    public Iterator<T> iterator() {
        try {
            populateServices();
        } catch (Exception ex) {
            ex.printStackTrace();
            services = Collections.emptyList();
        }
        return services.iterator();
    }

    @Override
    public String toString() {
        return "ServiceImpl{ Service class " +
               serviceName + " with filter " +
               filter.value() + '}';
    }
}
