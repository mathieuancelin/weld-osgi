package org.jboss.weld.environment.osgi.extension.services;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jboss.weld.environment.osgi.api.extension.Registration;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.jboss.weld.environment.osgi.api.extension.ServiceRegistry;
import org.jboss.weld.environment.osgi.api.extension.Services;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
        return new ServicesImpl<T>(contract, contract, registry);
    }

    @Override
    public <T> Service<T> getServiceReference(Class<T> contract) {
        return new ServiceImpl<T>(contract, bundle);
    }

}
