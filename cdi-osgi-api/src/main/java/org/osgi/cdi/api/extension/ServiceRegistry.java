package org.osgi.cdi.api.extension;

import javax.inject.Provider;

/**
 * <p>This interface represents a service registry where all OSGi services may be handled.</p> <p>It allows to:<ul>
 *     <li>
 * <p>Register a service implementation with a service, getting back the corresponding {@link Registration},
 * </p> </li>
 * <li> <p>Obtain the service implementations list as a {@link Service}</code>,</p> </li> <li> <p>Obtain an instance
 * provider for a specified type.</p> </li> </ul></p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Service
 * @see Registration
 */
public interface ServiceRegistry {

    /**
     * Register a service implementation.
     *
     * @param contract       the service contract interface.
     * @param implementation the service implementation class.
     * @param <T>            the service type.
     * @return the new service {@link Registration} or null if the registration goes wrong.
     */
    <T> Registration<T> registerService(Class<T> contract, Class<? extends T> implementation);

    /**
     * Register a service implementation.
     *
     * @param contract       the service contract interface.
     * @param implementation the service implementation class.
     * @param <T>            the service type.
     * @param <U>            the service implementation type.
     * @return the new service {@link Registration} or null if the registration goes wrong.
     */
    <T, U extends T> Registration<T> registerService(Class<T> contract, U implementation);

    /**
     * Get available service implementations of a service.
     *
     * @param contract the service contract interface that implementations are requested.
     * @param <T>      the service type.
     * @return the available service implementations as a {@link Service} or null if there is no such implementation.
     */
    <T> Service<T> getServiceReferences(Class<T> contract);

    /**
     * Get an instance provider for a specified type.
     *
     * @param unmanagedType the interface type of the requested instance producer.
     * @param <T>           the requested type.
     * @return a instance {@link Provider} for the specified type.
     */
    <T> Provider<T> newTypeInstance(Class<T> unmanagedType);
}
