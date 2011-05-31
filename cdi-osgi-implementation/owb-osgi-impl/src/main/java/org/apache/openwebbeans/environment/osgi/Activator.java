package org.apache.openwebbeans.environment.osgi;

import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class Activator implements BundleActivator {

    private CDIContainerFactory factory = new OWBCDIContainerFactory();
    private ServiceRegistration reg = null;

    @Override
    public void start(BundleContext context) throws Exception {
        reg = context.registerService(CDIContainerFactory.class.getName(), factory, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }
}
