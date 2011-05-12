package org.osgi.cdi.api.extension.events;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * <p>Represents all the CDI-OSGi bundle events as a superclass.</p> <p/> <p>Provides a way to listen all bundle
 * events
 * in a single method. Allows to retrieve both original event type as a {@link BundleEventType} and the firing
 * bundle.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see BundleEventType
 */
public abstract class AbstractBundleEvent {

    private final Bundle bundle;

    /**
     * Construct a new bundle event for the current bundle.
     *
     * @param bundle the firing bundle (current bundle).
     */
    public AbstractBundleEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Get the bundle event type.
     *
     * @return the {@link BundleEventType} of the fired bundle event.
     */
    public abstract BundleEventType getType();

    /**
     * Get the firing bundle id.
     *
     * @return the firing bundle id.
     */
    public long getBundleId() {
        return bundle.getBundleId();
    }

    /**
     * Get the firing bundle symbolic name.
     *
     * @return the firing bundle symbolic name.
     */
    public String getSymbolicName() {
        return bundle.getSymbolicName();
    }

    /**
     * Get the firing bundle version.
     *
     * @return the firing bundle version.
     */
    public Version getVersion() {
        return bundle.getVersion();
    }

    /**
     * Get the firing bundle.
     *
     * @return the firing bundle.
     */
    public Bundle getBundle() {
        return bundle;
    }
}
