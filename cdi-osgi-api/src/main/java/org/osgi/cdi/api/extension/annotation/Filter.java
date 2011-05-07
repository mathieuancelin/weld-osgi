package org.osgi.cdi.api.extension.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * <p>Qualifies an injection point with a LDAP filter for OSGi services.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {

    /**
     * The LDAP filter.
     *
     * @return the LDAP filter as a String.
     */
    String value();
}
