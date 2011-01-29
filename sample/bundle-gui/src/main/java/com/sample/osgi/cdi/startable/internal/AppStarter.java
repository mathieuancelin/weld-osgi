package com.sample.osgi.cdi.startable.internal;

import com.sample.osgi.cdi.startable.Starter;
import com.sample.osgi.cdi.gui.internal.SpellCheckerGui;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jboss.weld.environment.osgi.api.extension.Filter;
import org.jboss.weld.environment.osgi.api.extension.Publish;
import org.jboss.weld.environment.osgi.api.extension.Startable;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceArrival;

@Startable
@Publish
@ApplicationScoped
public class AppStarter implements Starter {

    @Inject SpellCheckerGui gui;

    @PostConstruct
    @Override
    public void init() {
        gui.start();
    }

    @PreDestroy
    @Override
    public void stop() {
        gui.stop();
    }

    public void listenServiceArrival(
            @Observes @Filter("javax.enterprise.inject.Instance")
            ServiceArrival arrival) {
        System.out.println("event");
        if (arrival.isTyped(Instance.class)) {
            arrival.type(Instance.class).getService().select(SpellCheckerGui.class);
        }
    }
}
