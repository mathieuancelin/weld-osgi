package com.sample.calculator.log;

import com.sample.calculator.api.CleanEvent;
import com.sample.calculator.api.NotificationEvent;
import com.sample.calculator.api.Operation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.osgi.cdi.api.extension.annotation.Specification;
import org.osgi.cdi.api.extension.events.InterBundleEvent;

@ApplicationScoped
public class ActionsLogger {

    private @Inject Event<InterBundleEvent> ibEvent;

    public void listenToEquals(@Observes @Specification(Operation.class) InterBundleEvent event) {
        Operation op = event.typed(Operation.class).get();

        ibEvent.fire(new InterBundleEvent(new NotificationEvent(op.getValue1() 
                + " " + op.getOperator().label()
                + " " + op.getValue2()
                + " = " + op.value())));
    }

    public void listenToClean(@Observes @Specification(CleanEvent.class) InterBundleEvent event) {
        ibEvent.fire(new InterBundleEvent(new NotificationEvent("Clean")));
    }
}
