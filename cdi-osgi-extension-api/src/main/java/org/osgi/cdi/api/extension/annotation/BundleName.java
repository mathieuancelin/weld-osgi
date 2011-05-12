package org.osgi.cdi.api.extension.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * <p>Qualifies an injection point for a specific bundle by its name.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleName {

    /**
     * The bundle symbolic name.
     *
     * @return the bundle symbolic name.
     */
    String value();
}
