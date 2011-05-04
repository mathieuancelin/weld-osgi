package org.osgi.cdi.api.extension;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu Clochard
 */
public interface Registration<T> extends Iterable<Registration<T>> {

    void unregister();
    <T> Service<T> getServiceReference();
    Service<T> select(Annotation... qualifiers);
    Service<T> select(String filter);
    <U extends T> Service<U> select(Class<U> subtype, Annotation... qualifiers);
    <U extends T> Service<U> select(Class<U> subtype, String filter);
    int size();
}
