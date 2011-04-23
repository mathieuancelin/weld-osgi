package org.jboss.weld.environment.osgi.api.extension;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface Service<T> extends Iterable<T> {

    T get();

//    Service<T> select(Annotation... qualifiers);
//
//    <U extends T> Service<U> select(Class<U> subtype, Annotation... qualifiers);
//
//    <U extends T> Service<U> select(TypeLiteral<U> subtype, Annotation... qualifiers);

    boolean isUnsatisfied();

    boolean isAmbiguous();

    int size();
}
