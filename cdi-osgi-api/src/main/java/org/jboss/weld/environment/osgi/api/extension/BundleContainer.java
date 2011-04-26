package org.jboss.weld.environment.osgi.api.extension;

import org.jboss.weld.environment.osgi.api.extension.events.InterBundleEvent;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface BundleContainer {

    void fire(InterBundleEvent event);
}
