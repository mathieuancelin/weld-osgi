package org.jboss.weld.environment.osgi.api.extension;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleVersion {
    String value();
}
