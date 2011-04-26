package org.osgi.cdi.api.integration;

import org.osgi.cdi.api.extension.events.InterBundleEvent;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface BundleContainer {

    void fire(InterBundleEvent event);
}
