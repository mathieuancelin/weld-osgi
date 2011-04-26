package org.jboss.weld.environment.osgi.api.extension;

import java.util.Collection;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface BundleContainers {

    Collection<BundleContainer> getContainers();
}
