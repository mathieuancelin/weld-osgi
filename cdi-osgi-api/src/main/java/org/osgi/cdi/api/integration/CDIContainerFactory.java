package org.osgi.cdi.api.integration;

import org.osgi.framework.Bundle;

import java.util.Set;

/**
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface CDIContainerFactory {

    Class<? extends CDIContainerFactory> delegateClass();

    String getID();

    Set<String> getContractBlacklist();

    CDIContainer container(Bundle bundle);
}
