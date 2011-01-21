package com.sample.osgi.cdi.gui;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jboss.weld.environment.osgi.integration.Startable;

@Startable
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
