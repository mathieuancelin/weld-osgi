package org.osgi.cdi.api.extension.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <p>Qualifies an injection point for a bundle header.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleHeader {

    /**
     * The name of the specific bundle header. Nondiscriminatory value for the typesafe resolution algorithm.
     *
     * @return the name of the bundle header.
     */
    @Nonbinding String value();
}
