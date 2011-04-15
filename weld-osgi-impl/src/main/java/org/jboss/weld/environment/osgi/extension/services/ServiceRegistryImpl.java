package org.jboss.weld.environment.osgi.extension.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.jboss.weld.environment.osgi.api.extension.BundleState;
import org.jboss.weld.environment.osgi.api.extension.Registration;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.jboss.weld.environment.osgi.api.extension.ServiceRegistry;
import org.jboss.weld.environment.osgi.api.extension.Services;
import org.jboss.weld.environment.osgi.api.extension.events.AbstractServiceEvent;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.events.Invalid;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceArrival;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceChanged;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceDeparture;
import org.jboss.weld.environment.osgi.api.extension.events.Valid;
import org.jboss.weld.environment.osgi.extension.CDIOSGiExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
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
    private RegistrationsHolder holder;

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

    private  Set<Class<?>> osgiServiceDependencies;
    
    private Map<Class<?>, Beantype<?>> types = new HashMap<Class<?>, Beantype<?>>();

    @Override
    public <T> Registration<T> registerService(Class<T> contract, Class<? extends T> implementation) {
        ServiceRegistration reg = registry.registerService(contract.getName(),
                instances.select(implementation).get(), null);
        holder.addRegistration(reg);
        return new RegistrationImpl<T>(
                contract, reg, registry, bundle, holder);
    }

    @Override
    public <T, U extends T> Registration<T> registerService(Class<T> contract, U implementation) {
        ServiceRegistration reg = registry.registerService(contract.getName(),
                implementation, null);
        holder.addRegistration(reg);
        return new RegistrationImpl<T>(
                contract, reg, registry, bundle, holder);
    }

    @Override
    public <T> Services<T> getServiceReferences(Class<T> contract) {
        return new ServicesImpl<T>(contract, registry);
    }

    @Override
    public <T> Service<T> getServiceReference(Class<T> contract) {
        return new ServiceImpl<T>(contract, bundle);
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

    @Override
    public <T> Provider<T> newTypeInstance(Class<T> unmanagedType) {
        if (!types.containsKey(unmanagedType)) {
            types.put(unmanagedType, new Beantype<T>(unmanagedType, manager));
        }
        return (Provider<T>) types.get(unmanagedType);
    }

    public void listenStartup(@Observes BundleContainerInitialized event) {
        osgiServiceDependencies = extension.getRequiredOsgiServiceDependencies();
        checkForValidDependencies(null);
    }

    public void bind(@Observes ServiceArrival arrival) {
        checkForValidDependencies(arrival);
    }

    public void changed(@Observes ServiceChanged changed) {
        checkForValidDependencies(changed);
    }

    public void unbind(@Observes ServiceDeparture departure) {
        checkForValidDependencies(departure);
    }

    private void checkForValidDependencies(AbstractServiceEvent event) {
        if (event == null || applicable(event.getServiceClasses())) {
            boolean valid = true;
            if (osgiServiceDependencies.isEmpty()) {
                valid = false;
            } else {
                for (Class<?> clazz : osgiServiceDependencies) {
                    try {
                        ServiceReference[] refs = registry.getServiceReferences(clazz.getName(), null);
                        if (refs != null) {
                            int available = refs.length;
                            if (available <= 0) {
                                valid = false;
                            }
                        } else {
                            valid = false;
                        }
                    } catch (InvalidSyntaxException ex) {
                        // nothing here
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
            if (osgiServiceDependencies.contains(clazz)) {
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
