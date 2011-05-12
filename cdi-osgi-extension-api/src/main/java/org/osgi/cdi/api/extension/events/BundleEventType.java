package org.osgi.cdi.api.extension.events;

/**
 * <p>Represents all possible bundle state for a bundle event.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see AbstractBundleEvent
 */
public enum BundleEventType {

    INSTALLED,
    LAZY_ACTIVATION,
    RESOLVED,
    STARTED,
    STARTING,
    STOPPED,
    STOPPING,
    UNINSTALLED,
    UNRESOLVED,
    UPDATED,
}
