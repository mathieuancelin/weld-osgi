package com.sample.osgi.cdi.startable.internal;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jboss.weld.environment.osgi.api.extension.Publish;
import org.jboss.weld.environment.osgi.api.extension.Startable;

@Startable
@Publish
public class ServiceImpl {

    @PostConstruct
    public void init() {
        System.out.println("init");
    }

    @PreDestroy
    public void stop() {
        System.out.println("stop");
    }
}
