package org.osgi.cdi.api.extension.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <p>Qualifies an injection point for a OSGi bundle.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface OSGiBundle {

    /**
     * The symbolic name of the bundle. Nondiscriminatory value for the typesafe resolution algorithm.
     *
     * @return the symbolic name of the bundle.
     */
    @Nonbinding String value();

    /**
     * The version of the bundle. Nondiscriminatory value for the typesafe resolution algorithm.
     *
     * @return the version of the bundle.
     */
    @Nonbinding String version() default "";
}
