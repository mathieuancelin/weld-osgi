package com.sample.osgi.cdi.activator;

import com.sample.osgi.cdi.gui.SpellCheckerGui;
import org.jboss.weld.environment.osgi.WeldContainer;
import org.jboss.weld.environment.osgi.WeldContainerFetcher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN
 */
public class GuiActivator implements BundleActivator {

    private SpellCheckerGui gui;
    private WeldContainer weld;

    @Override
    public void start(BundleContext context) throws Exception {
        ServiceReference ref = context.getServiceReference(WeldContainerFetcher.class.getName());
        if (ref != null) {
            WeldContainerFetcher fetcher = (WeldContainerFetcher) context.getService(ref);
            weld = fetcher.getContainer(context.getBundle());
            gui = weld.instance().select(SpellCheckerGui.class).get();
            gui.start();
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {}
}
