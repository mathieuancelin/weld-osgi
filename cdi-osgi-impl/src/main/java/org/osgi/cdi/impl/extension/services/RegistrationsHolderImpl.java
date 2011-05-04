package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.RegistrationHolder;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu Clochard
 */
@ApplicationScoped
public class RegistrationsHolderImpl implements RegistrationHolder {

    private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();

    @Override public List<ServiceRegistration> getRegistrations() {
        return registrations;
    }

    @Override public void addRegistration(ServiceRegistration reg) {
        registrations.add(reg);
    }

    @Override public void removeRegistration(ServiceRegistration reg) {
        registrations.remove(reg);
    }

    @Override public void clear() {
        registrations.clear();
    }

    @Override public int size() {
        return registrations.size();
    }

}
