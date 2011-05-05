package org.osgi.cdi.api.extension.events;

import org.osgi.framework.BundleContext;

/**
 * <p>Represents all the CDI-OSGi bundle container events as a superclass.</p> <p/> <p>Provides a way to listen all
 * bundle container events in a single method. Allows to retrieve both original event type as a {@link
 * BundleContainerEventType} and the firing bundle context.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see BundleContainerEventType
 */
public abstract class AbstractBundleContainerEvent {

    private BundleContext bundleContext;

    /**
     * Construct a new bundle container event for the current bundle.
     *
     * @param context the firing bundle context (current bundle context).
     */
    public AbstractBundleContainerEvent(final BundleContext context) {
        this.bundleContext = context;
    }

    /**
     * Get the firing bundle context.
     *
     * @return the firing {@link org.osgi.framework.BundleContext}.
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * Get the bundle container event type.
     *
     * @return the {@link BundleContainerEventType} of the fired bundle container event.
     */
    public abstract BundleContainerEventType getType();

}
