package org.osgi.cdi.api.extension;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface Registration<T> {

    void unregister();

    <T> Service<T> getServiceReference();

}
