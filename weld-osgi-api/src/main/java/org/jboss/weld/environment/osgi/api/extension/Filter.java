package org.jboss.weld.environment.osgi.api.extension;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author mathieu
 */
@Target({ PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    String value() default "";
}
