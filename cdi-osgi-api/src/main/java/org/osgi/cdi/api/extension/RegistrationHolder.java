package org.osgi.cdi.api.extension;

import org.osgi.framework.ServiceRegistration;

import java.util.List;

/**
 *
 * @author Matthieu Clochard
 */
public interface RegistrationHolder {

    List<ServiceRegistration> getRegistrations();
    void addRegistration(ServiceRegistration reg);
    void removeRegistration(ServiceRegistration reg);
    void clear();
    int size();
}
