package org.osgi.cdi.api.extension;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;

/**
 * <p>Represents a service instance producer parametrized by the service to inject. It has the same behavior than CDI
 * Instance<T> except that it represents only OSGi service beans.</p>
 * <p/>
 * <p>A OSGi service may not be subtyped</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD
 * @see Instance
 * @see javax.inject.Provider
 */
// should extends Instance<T> but weld doesn't seems to like it
public interface Service<T> extends Iterable<T> {

    T get();

    Service<T> select(Annotation... qualifiers);

    <U extends T> Service<U> select(Class<U> subtype, Annotation... qualifiers);

    <U extends T> Service<U> select(TypeLiteral<U> subtype, Annotation... qualifiers);

    boolean isUnsatisfied();

    boolean isAmbiguous();

    int size();
}
