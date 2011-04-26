package org.osgi.cdi.api.integration;

import org.osgi.framework.Bundle;

import java.util.Set;

/**
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface BundleContainerFactory {

    Class<? extends BundleContainerFactory> delegateClass();

    String getID();

    Set<String> getContractBlacklist();

    BundleContainer container(Bundle bundle);

}
