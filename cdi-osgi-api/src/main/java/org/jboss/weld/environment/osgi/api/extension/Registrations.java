package org.jboss.weld.environment.osgi.api.extension;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface Registrations<T> extends Iterable<Registration<T>> {

    int size();
}
