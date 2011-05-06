package org.osgi.cdi.api.extension.events;

import javax.inject.Provider;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class InterBundleEvent {

    private final Object event;

    private boolean sent = false;

    private Class<?> type;

    public InterBundleEvent(Object event) {
        this.event = event;
    }

    public InterBundleEvent(Object event, Class<?> type) {
        this.event = event;
        this.type = type;
    }

    public Class<?> type() {
        if (type != null) {
            return type;
        } else {
            return event.getClass();
        }
    }

    public boolean isTyped(Class<?> type) {
        if (this.type != null) {
            return this.type.equals(type);
        } else {
            return event.getClass().equals(type);
        }
    }

    public <T> Provider<T> typed(Class<T> type) {
        if (isTyped(type)) {
            return new Provider<T>() {
                @Override
                public T get() {
                    return (T) event;
                }
            };
        } else {
            throw new RuntimeException("The event is not of type " + type.getName());
        }
    }

    public Object get() {
        return event;
    }

    public boolean isSent() {
        return sent;
    }

    public void sent() {
        this.sent = true;
    }
}
