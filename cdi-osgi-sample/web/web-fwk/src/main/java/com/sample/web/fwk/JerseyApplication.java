package com.sample.web.fwk;

import java.util.Set;
import javax.ws.rs.core.Application;

public class JerseyApplication extends Application {

    private Set<Class<?>> classes;

    public void setClasses(Set<Class<?>> classes) {
        this.classes = classes;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
