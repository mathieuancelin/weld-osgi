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

import org.osgi.cdi.api.extension.BundleState;
import org.osgi.cdi.api.extension.Registration;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.ServiceRegistry;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.events.*;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.framework.*;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

/**
 * Implementation of {@link ServiceRegistry}.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
@ApplicationScoped
public class ServiceRegistryImpl implements ServiceRegistry {

    @Inject
    private BundleContext registry;

    @Inject
    private Bundle bundle;

    @Inject
    private Instance<Object> instances;
    
    @Inject
    private RegistrationsHolderImpl holder;

    @Inject
    private BeanManager manager;

    @Inject
    private Event<Valid> validEvent;

    @Inject
    private Event<Invalid> invalidEvent;

    @Inject
    private CDIOSGiExtension extension;

    @Inject
    private BundleHolder bundleHolder;

    private  Map<Class, Set<Filter>> osgiServiceDependencies;
    
    private Map<Class<?>, Beantype<?>> types = new HashMap<Class<?>, Beantype<?>>();

    @Override
    public <T> Registration<T> registerService(Class<T> contract, Class<? extends T> implementation) {
        ServiceRegistration reg = registry.registerService(contract.getName(),
                instances.select(implementation).get(), null);
        holder.addRegistration(reg);
        return new RegistrationImpl<T>(
                contract, registry, bundle, holder);
    }

    @Override
    public <T, U extends T> Registration<T> registerService(Class<T> contract, U implementation) {
        ServiceRegistration reg = registry.registerService(contract.getName(),
                implementation, null);
        holder.addRegistration(reg);
        return new RegistrationImpl<T>(
                contract, registry, bundle, holder);
    }

    @Override
    public <T> Service<T> getServiceReferences(Class<T> contract) {
        return new ServiceImpl<T>(contract, registry);
    }

    @PreDestroy
    public void stop() {
        for (Beantype<?> type : types.values()) {
            type.destroy();
        }
    }

    public <T> void registerNewType(Class<T> type) {
        if (!types.containsKey(type)) {
            types.put(type, new Beantype<T>(type, manager));
        }
    }

    public void listenStartup(@Observes BundleContainerEvents.BundleContainerInitialized event) {
        osgiServiceDependencies = extension.getRequiredOsgiServiceDependencies();
        checkForValidDependencies(null);
    }

    public void bind(@Observes ServiceEvents.ServiceArrival arrival) {
        checkForValidDependencies(arrival);
    }

    public void changed(@Observes ServiceEvents.ServiceChanged changed) {
        checkForValidDependencies(changed);
    }

    public void unbind(@Observes ServiceEvents.ServiceDeparture departure) {
        checkForValidDependencies(departure);
    }

    private void checkForValidDependencies(AbstractServiceEvent event) {
        if (event == null || applicable(event.getServiceClasses(getClass()))) {
            boolean valid = true;
            if (!osgiServiceDependencies.isEmpty()) {
                invalid:
                for (Map.Entry<Class, Set<Filter>> entry : osgiServiceDependencies.entrySet()) {
                    Class clazz = entry.getKey();
                    for (Filter filter : entry.getValue()) {
                        try {
                             ServiceReference[] refs = null;
                            if(filter != null && filter.value() != null && filter.value().length() > 0) {
                                refs = registry.getServiceReferences(clazz.getName(), filter.value());
                            } else {
                                refs = registry.getServiceReferences(clazz.getName(), null);
                            }
                            if (refs != null) {
                                int available = refs.length;
                                if (available <= 0) {
                                    valid = false;
                                    break invalid;
                                }
                            } else {
                                valid = false;
                                break invalid;
                            }
                        } catch (InvalidSyntaxException ex) {
                            valid = false;
                            break invalid;
                        }
                    }
                }
            }
            // TODO : synchronize here to change the state of the bundle
            if (valid && bundleHolder.getState().equals(BundleState.INVALID)) {
                bundleHolder.setState(BundleState.VALID);
                validEvent.fire(new Valid());
            } else if (!valid && bundleHolder.getState().equals(BundleState.VALID)) {
                bundleHolder.setState(BundleState.INVALID);
                invalidEvent.fire(new Invalid());
            }
        }
    }

    private boolean applicable(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (osgiServiceDependencies.containsKey(clazz)) {
                return true;
            }
        }
        return false;
    }

    private class Beantype<T> implements Provider<T> {

        private final Class<T> clazz;
        private final BeanManager manager;
        private final AnnotatedType annoted;
        private final InjectionTarget it;
        private final CreationalContext<?> cc;
        private Collection<T> instances = new ArrayList<T>();

        public Beantype(Class<T> clazz, BeanManager manager) {
            this.clazz = clazz;
            this.manager = manager;
            annoted = manager.createAnnotatedType(clazz);
            it = manager.createInjectionTarget(annoted);
            cc = manager.createCreationalContext(null);
        }

        public void destroy() {
            for (T instance : instances) {
                it.preDestroy(instance);
                it.dispose(instance);
            }
            cc.release();
        }

        @Override
        public T get() {
            T instance = (T) it.produce(cc);
            it.inject(instance, cc);
            it.postConstruct(instance);
            instances.add(instance);
            return instance;
        }
    }
}
