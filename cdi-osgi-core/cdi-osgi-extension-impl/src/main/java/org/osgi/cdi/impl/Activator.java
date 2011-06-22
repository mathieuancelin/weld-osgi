package org.osgi.cdi.impl;

import org.osgi.cdi.impl.extension.ExtensionActivator;
import org.osgi.cdi.impl.integration.IntegrationActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This is the {@link BundleActivator} of the extension bundle. It represents the entry point of CDI-OSGi.
 * <p/>
 * It is responsible for starting both extension and integration part of CDI-OSGi. First the extension is started, then
 * the integration.
 * It also stops both part when CDI-OSGi shutdown.
 *
 * @author Guillaume Sauthier
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class Activator implements BundleActivator {

    private BundleActivator integration = new IntegrationActivator();

    private BundleActivator extension = new ExtensionActivator();

    @Override
    public void start(BundleContext context) throws Exception {
        integration.start(context);
        extension.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        extension.stop(context);
        integration.stop(context);
    }
}
