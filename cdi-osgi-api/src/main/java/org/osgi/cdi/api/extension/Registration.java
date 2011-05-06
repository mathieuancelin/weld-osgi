package org.osgi.cdi.api.extension;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;

/**
 * <p>This interface represents the registrations of a injectable service in the service resgistry. Its
 * fonctionnement
 * is similar to {@link Service}, thus it might represent the iterable set of all the registrations of a service.</p>
 * <p>It allows to:<ul> <li> <p>Wrap a list of service registration (i.e. the bindings between a service and its
 * implementations) as an {@link Iterable} java object,</p> </li> <li> <p>Select a subset of these registration
 * filtered
 * by {@link javax.inject.Qualifier}s or LDAP filters,</p> </li> <li> <p>Iterate through these service
 * registrations,</p> </li> <li> <p>Obtain the service implementations list as a {@link Service},
 * </p> </li> <li> <p>Get
 * the number of registration (i.e the number of registered service implementations).</p> </li> </ul></p> <p>OSGi
 * services should not be subtyped.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see ServiceRegistry
 * @see Service
 * @see RegistrationHolder
 */
public interface Registration<T> extends Iterable<Registration<T>> {

    /**
     * Unregister all the service implementations in this registration.
     */
    void unregister();

    /**
     * Get all the service implementation in this registration.
     *
     * @param <T> the type of the concerned service.
     * @return all the service implementations as a {@link Service}.
     */
    <T> Service<T> getServiceReference();

    /**
     * Get a subset of this registration with particular service implementations.
     *
     * @param qualifiers the {@link javax.inject.Qualifier} annotations that filter the requested implementation.
     * @return a {@link Registration} that is a subset of this registration with the matching service implementations.
     */
    Registration<T> select(Annotation... qualifiers);

    /**
     * Get a subset of this registration with particular service implementations.
     *
     * @param filter the LDAP filter that filters the requested implementation.
     * @return a {@link Registration} that is a subset of this registration with the matching service implementations.
     */
    Registration<T> select(String filter);

    int size();
}
