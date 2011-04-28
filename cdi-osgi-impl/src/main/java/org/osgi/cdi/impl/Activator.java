package org.osgi.cdi.impl;

import org.osgi.cdi.impl.extension.ExtensionActivator;
import org.osgi.cdi.impl.integration.IntegrationActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 27/01/11
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
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
