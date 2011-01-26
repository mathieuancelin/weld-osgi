package org.jboss.weld.environment.osgi.api.integration;

import org.osgi.framework.Bundle;

/**
 *
 * @author mathieu
 */
public interface CDIOSGiContainerFactory {
    CDIOSGiContainer getContainer(Bundle bundle);
}
