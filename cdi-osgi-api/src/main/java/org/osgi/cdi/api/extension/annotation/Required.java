package org.osgi.cdi.api.extension.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * <p>Qualifies an injection point for a required OSGi service for the current bundle.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
}
