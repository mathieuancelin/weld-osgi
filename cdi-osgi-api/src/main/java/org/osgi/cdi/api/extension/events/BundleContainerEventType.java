package org.osgi.cdi.api.extension.events;

/**
 * <p>Represents all possible bundle container state for a bundle container event.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see AbstractBundleContainerEvent
 */
public enum BundleContainerEventType {
    INITIALIZED,
    SHUTDOWN
}
