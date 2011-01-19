package org.jboss.weld.environment.osgi.integration;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Startable {}
