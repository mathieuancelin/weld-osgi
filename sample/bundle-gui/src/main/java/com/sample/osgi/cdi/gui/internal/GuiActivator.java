package com.sample.osgi.cdi.gui.internal;

import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN
 */
public class GuiActivator implements BundleActivator {

    private SpellCheckerGui gui;

    @Override
    public void start(BundleContext context) throws Exception {
        ServiceReference ref = context.getServiceReference(CDIOSGiContainer.class.getName());
        if (ref != null) {
            CDIOSGiContainer container = (CDIOSGiContainer) context.getService(ref);
//            weld = fetcher.getContainer(context.getBundle());
//            gui = weld.instance().select(SpellCheckerGui.class).get();
//            gui.start();
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {}
}
