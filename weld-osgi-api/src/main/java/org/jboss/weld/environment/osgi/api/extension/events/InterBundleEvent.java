package org.jboss.weld.environment.osgi.api.extension.events;

/**
 *
 * @author mathieuancelin
 */
public class InterBundleEvent {

    private final Object event;

    private boolean sent = false;

    public InterBundleEvent(Object event) {
        this.event = event;
    }

    public Object getEvent() {
        return event;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
