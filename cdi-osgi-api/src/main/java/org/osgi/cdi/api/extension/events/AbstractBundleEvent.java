package org.osgi.cdi.api.extension.events;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public abstract class AbstractBundleEvent {
    
    public static enum EventType {
        INSTALLED,LAZY_ACTIVATION,RESOLVED,STARTED,STARTING,
        STOPPED,STOPPING,UNINSTALLED,UNRESOLVED,UPDATED,
    }

    private final Bundle bundle;

    public AbstractBundleEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    public abstract EventType getType();

    public long getBundleId() {
        return bundle.getBundleId();
    }

    public String getSymbolicName() {
        return bundle.getSymbolicName();
    }

    public Version getVersion() {
        return bundle.getVersion();
    }

    public Bundle getBundle() {
        return bundle;
    }
}
