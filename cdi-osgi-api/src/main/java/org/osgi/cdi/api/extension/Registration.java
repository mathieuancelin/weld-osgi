package org.osgi.cdi.api.extension;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu Clochard
 */
public interface Registration<T> extends Iterable<Registration<T>> {

    void unregister();
    <T> Service<T> getServiceReference();
    int size();
}
