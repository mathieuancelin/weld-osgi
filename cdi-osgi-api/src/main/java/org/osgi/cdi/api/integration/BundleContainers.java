package org.osgi.cdi.api.integration;

import org.osgi.cdi.api.integration.BundleContainer;

import java.util.Collection;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface BundleContainers {

    Collection<BundleContainer> getContainers();
}
