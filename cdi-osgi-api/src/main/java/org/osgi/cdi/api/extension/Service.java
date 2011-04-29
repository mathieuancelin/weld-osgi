package org.osgi.cdi.api.extension;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
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
