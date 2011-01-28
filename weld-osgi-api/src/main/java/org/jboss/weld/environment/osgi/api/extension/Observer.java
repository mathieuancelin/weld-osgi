package org.jboss.weld.environment.osgi.api.extension;


import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.interceptor.InterceptorBinding;
import static java.lang.annotation.ElementType.TYPE;

@InterceptorBinding
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Observer {

}
