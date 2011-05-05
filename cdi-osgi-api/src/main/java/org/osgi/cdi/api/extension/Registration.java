package org.osgi.cdi.api.extension;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;

/**
 * <p>Represents the binding between a service and its registered implementations, the registrations of a injectable
 * OSGi services in the service resgistry.</p> <p/> <p>Its fonctionnement is similar to Service<T>, thus it might
 * represent the iterable set of all the registration of a service. It allows to select a particular subset of
 * registrations, to unregister service implementations or to obtain a corresponding service references.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu Clochard
 * @see ServiceRegistry
 * @see Service
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
