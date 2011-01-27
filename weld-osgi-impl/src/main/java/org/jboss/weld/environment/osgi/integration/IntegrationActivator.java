package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainerFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 27/01/11
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        WeldFactory factory = new WeldFactory();
        context.registerService(CDIOSGiContainerFactory.class.getName(),
                                factory,
                                null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
