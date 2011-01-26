package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainer;
import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainerFactory;
import org.osgi.framework.Bundle;

/**
 *
 * @author mathieu
 */
public class WeldFactory implements CDIOSGiContainerFactory {

    @Override
    public CDIOSGiContainer getContainer(Bundle bundle) {
        return new Weld(bundle);
    }
}
