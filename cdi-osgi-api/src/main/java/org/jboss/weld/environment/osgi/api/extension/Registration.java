package org.jboss.weld.environment.osgi.api.extension;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface Registration<T> {

    void unregister();

    <T> Service<T> getServiceReference();

}
