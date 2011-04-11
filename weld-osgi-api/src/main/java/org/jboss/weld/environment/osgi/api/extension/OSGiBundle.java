package org.jboss.weld.environment.osgi.api.extension;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface OSGiBundle {
    /**
     * @return the symbolic name of the bundle
     */
    @Nonbinding
    String value();

    @Nonbinding
    String version() default "";
}
