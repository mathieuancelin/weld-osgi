package org.osgi.cdi.api.extension.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Identifies a OSGi service implementation.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish {

    /**
     * The contracts the annotated class fulfills.
     *
     * @return the contracts of the annotated implementation as an array of interfaces.
     */
    public Class[] contracts() default {};

    /**
     * The properties of the annotated class as OSGi service properties (for LDAP filtering).
     *
     * @return the properties of the service implementation.
     */
    public String[] properties() default {};

    /**
     * If the qualifiers are converted into properties for this annotated service implementation.
     *
     * @return false if the {@link javax.inject.Qualifier}s are ignored as properties, true otherwise.
     */
    public boolean useQualifiersAsProperties() default false;
}
