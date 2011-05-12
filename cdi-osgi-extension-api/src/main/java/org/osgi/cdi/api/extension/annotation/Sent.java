package org.osgi.cdi.api.extension.annotation;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * <p>Qualifies an injection point for a event communication from outside the current bundle.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
@Qualifier
@Target({PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sent {
}
