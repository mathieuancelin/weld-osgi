package org.jboss.weld.environment.osgi.api.extension;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Qualifier
@Target({FIELD,METHOD,PARAMETER,TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish {
    @Nonbinding
    public Class[] contracts() default {};
}
