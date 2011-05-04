package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.Registration;
import org.osgi.cdi.api.extension.RegistrationHolder;
import org.osgi.cdi.api.extension.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu Clochard
 */
public class RegistrationImpl<T> implements Registration<T> {

    private final Class<T> contract;
    private final BundleContext registry;
    private final Bundle bundle;
    private final RegistrationHolder holder;
    private List<Registration<T>> registrations = new ArrayList<Registration<T>>();

    public RegistrationImpl(Class<T> contract,
            BundleContext registry, Bundle bundle,
            RegistrationHolder holder) {
        this.contract = contract;
        this.registry = registry;
        this.holder = holder;
        this.bundle = bundle;
    }

    @Override
    public void unregister() {
        for(ServiceRegistration reg : holder.getRegistrations()) {
            holder.removeRegistration(reg);
            reg.unregister();
        }
    }

    @Override
    public <T> Service<T> getServiceReference() {
        return new ServiceImpl<T>(contract, registry);
    }

    @Override
    public int size() {
        return holder.size();
    }

    @Override
    public Iterator<Registration<T>> iterator() {
        populate();
        return registrations.iterator();
    }

    private void populate() {
        registrations.clear();
        try {
            List<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                registrations.add(new RegistrationImpl<T>(contract, registry, bundle, holder));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
