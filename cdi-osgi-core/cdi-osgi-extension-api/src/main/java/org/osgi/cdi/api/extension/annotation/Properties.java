package org.osgi.cdi.api.extension.annotation;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * <p>This annotation qualifies a class that represents a LDAP filtered service.</p>
 * <p>It allows to specify multiple LDAP properties, as a array of {@link Property}.</p>
 * <p>It may be coupled with a {@link Publish} annotation in order to qualify the published service implementations. The
 * LDAP filtering acts on {@link Qualifier} or {@link Properties} annotations or regular OSGi LDAP properties used in
 * service publishing.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Filter
 * @see Qualifier
 * @see Property
 * @see Publish
 * @see org.osgi.cdi.api.extension.Service
 * @see org.osgi.cdi.api.extension.ServiceRegistry
 */
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface Properties {

    /**
     * The properties of the annotated class as OSGi service properties (for LDAP filtering).
     *
     * @return the properties of the service implementation as an array of {@link Property}.
     */
    public Property[] value() default {};

}
