package org.jboss.weld.environment.osgi.api.extension;

import javax.inject.Provider;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface ServiceRegistry {

    <T> Registration<T> registerService(Class<T> contract, Class<? extends T> implementation);
    <T, U extends T> Registration<T> registerService(Class<T> contract, U implementation);

    <T> Services<T> getServiceReferences(Class<T> contract);
    <T> Service<T> getServiceReference(Class<T> contract);

    <T> Provider<T> newTypeInstance(Class<T> unmanagedType);

}
