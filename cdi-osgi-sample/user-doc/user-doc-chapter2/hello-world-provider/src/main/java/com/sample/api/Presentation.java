package com.sample.api;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ TYPE, METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InterceptorBinding
public @interface Presentation {
}
