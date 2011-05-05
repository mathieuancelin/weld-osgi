package org.osgi.cdi.api.extension.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <p>Qualifies an injection point for a data file of the current bundle.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleDataFile {

    /**
     * The data file that should be injected. Nondiscriminatory value for the typesafe resolution algorithm.
     *
     * @return the relative path of the data file in the current bundle.
     */
    @Nonbinding String value();
}
