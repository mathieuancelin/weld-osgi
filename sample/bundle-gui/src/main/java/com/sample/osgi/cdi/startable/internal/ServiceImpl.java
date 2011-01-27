package com.sample.osgi.cdi.startable.internal;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.weld.environment.osgi.api.extension.Publish;
import org.jboss.weld.environment.osgi.api.extension.Startable;

@Startable
@Publish
@ApplicationScoped
public class ServiceImpl {

    @PostConstruct
    public void init() {
        System.out.println("init");
    }

    public void call() {
        System.out.println("call");
    }

    @PreDestroy
    public void stop() {
        System.out.println("stop");
    }
}
