package com.sample.osgi.cdi.startable.internal;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import org.jboss.weld.environment.osgi.api.extension.Publish;
import org.jboss.weld.environment.osgi.api.extension.Startable;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

@Startable
@Publish
public class ServiceImpl {

    @PostConstruct
    public void init() {
        System.out.println("init");
    }

    public void listen(@Observes ServiceEvent event) {
        System.out.println("listen " + event);
    }

    public void listen2(@Observes BundleEvent event) {
        System.out.println("listen " + event);
    }

    @javax.annotation.PreDestroy
    public void stop() {
        System.out.println("stop");
    }
}
