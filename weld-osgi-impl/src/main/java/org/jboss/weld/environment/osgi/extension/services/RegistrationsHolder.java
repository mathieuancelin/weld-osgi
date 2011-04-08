package org.jboss.weld.environment.osgi.extension.services;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class RegistrationsHolder {

    private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();

    public List<ServiceRegistration> getRegistrations() {
        return registrations;
    }

    public void addRegistration(ServiceRegistration reg) {
        registrations.add(reg);
    }

    public void removeRegistration(ServiceRegistration reg) {
        registrations.remove(reg);
    }

    public void clear() {
        registrations.clear();
    }

}
