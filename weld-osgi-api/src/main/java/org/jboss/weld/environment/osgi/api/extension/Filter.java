package org.jboss.weld.environment.osgi.api.extension;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Qualifier
@Target({ PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    String value() default "";
}
