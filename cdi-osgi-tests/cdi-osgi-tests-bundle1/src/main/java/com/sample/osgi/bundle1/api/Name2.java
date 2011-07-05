package com.sample.osgi.bundle1.api;

import org.osgi.cdi.api.extension.annotation.Filter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Filter("(Name.value=2)")
@Retention(RetentionPolicy.RUNTIME)
public @interface Name2 {
}
