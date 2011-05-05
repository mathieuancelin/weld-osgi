package org.osgi.cdi.api.extension;

import org.osgi.framework.ServiceRegistration;

import java.util.List;

/**
 * <p>Represents a group of service registrations</p> <p>This interface wraps the OSGi ServiceRegistrations in order
 * to
 * be used by CDI-OSGi Registration. It provides utility methods to handle the registration list of a given
 * service.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD
 * @see Registration
 * @see ServiceRegistration
 */
public interface RegistrationHolder {

    /**
     * Get all service registration in this registration holder.
     *
     * @return the list of {@link ServiceRegistration} in this holder.
     */
    List<ServiceRegistration> getRegistrations();

    /**
     * Add a service registration in this registration holder.
     *
     * @param registration the {@link ServiceRegistration} to add to this holder.
     */
    void addRegistration(ServiceRegistration registration);

    /**
     * Remove a service registration from this registration holder.
     *
     * @param registration the {@link ServiceRegistration} to remove from this holder.
     */
    void removeRegistration(ServiceRegistration registration);

    /**
     * Clear this registration holder, removing all its contained {@link ServiceRegistration}s.
     */
    void clear();

    /**
     * Get the number of service registrations in this registration holder.
     *
     * @return the number of {@link ServiceRegistration} in this holder.
     */
    int size();
}
