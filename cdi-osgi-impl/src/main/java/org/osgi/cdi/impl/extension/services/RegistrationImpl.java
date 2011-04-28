package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.Registration;
import org.osgi.cdi.api.extension.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class RegistrationImpl<T> implements Registration<T> {

    private final ServiceRegistration reg;
    private final Class<T> contract;
    private final BundleContext registry;
    private final RegistrationsHolder holder;
    private final Bundle bundle;

    public RegistrationImpl(Class<T> contract, 
            ServiceRegistration reg, 
            BundleContext registry, Bundle bundle,
            RegistrationsHolder holder) {
        this.reg = reg;
        this.contract = contract;
        this.registry = registry;
        this.holder = holder;
        this.bundle = bundle;
    }

    @Override
    public void unregister() {
        holder.removeRegistration(reg);
        reg.unregister();
    }

    @Override
    public <T> Service<T> getServiceReference() {
        return new ServiceImpl<T>(contract, registry);
    }
}
