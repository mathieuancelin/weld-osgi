package org.osgi.cdi.api.extension.annotation;

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
    public String[] properties() default {};
    public boolean useQualifiersAsProperties() default false;
}
