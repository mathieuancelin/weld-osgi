package org.jboss.weld.environment.osgi.api.extension;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish {
    public Class[] contracts() default {};
}
