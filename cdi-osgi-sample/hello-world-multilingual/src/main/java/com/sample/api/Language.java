package com.sample.api;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface Language {
    String value() default "ENGLISH";
}
