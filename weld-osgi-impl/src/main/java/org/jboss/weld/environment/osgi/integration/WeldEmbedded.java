package org.jboss.weld.environment.osgi.integration;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.jboss.weld.environment.osgi.api.extension.BundleContainer;
import org.jboss.weld.environment.osgi.api.extension.BundleContainers;
import org.jboss.weld.environment.osgi.api.extension.events.InterBundleEvent;
import org.jboss.weld.environment.osgi.extension.ExtensionActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldEmbedded {

    private final Weld weld;
    private final ExtensionActivator activator;
    private final BundleContext context;
    private Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();

    private WeldEmbedded(Weld weld, ExtensionActivator activator, BundleContext context) {
        this.weld = weld;
        this.activator = activator;
        this.context = context;
    }

    public static WeldEmbedded startFor(BundleContext context) throws Exception {
        boolean set = BundleSingletonProvider.currentBundle.get() != null;
        BundleSingletonProvider.currentBundle.set(context.getBundle().getBundleId());
        WeldEmbedded embedded =
                new WeldEmbedded(new Weld(context.getBundle()),
                new ExtensionActivator(), context);
        try {
            embedded.regs.add(
                    context.registerService(Event.class.getName(),
                    embedded.weld.getEvent(),
                    null));

            embedded.regs.add(
                    context.registerService(BeanManager.class.getName(),
                    embedded.weld.getBeanManager(),
                    null));

            embedded.regs.add(
                    context.registerService(Instance.class.getName(),
                    embedded.weld.getInstance(),
                    null));
        } catch (Throwable t) {
            // Ignore
        }
        embedded.weld.initialize(new BundleContainer() {

                @Override
                public void fire(InterBundleEvent event) {
                    // nothing to do
                }
            }, new BundleContainers() {

            @Override
            public Collection<BundleContainer> getContainers() {
                return Collections.emptyList();
            }
        });
        embedded.activator.start(context);
        if (!set) {
            BundleSingletonProvider.currentBundle.remove();
        }
        return embedded;
    }

    public void shutdown() throws Exception {
        boolean set = BundleSingletonProvider.currentBundle.get() != null;
        BundleSingletonProvider.currentBundle.set(context.getBundle().getBundleId());
        activator.stop(context);
        for (ServiceRegistration reg : regs) {
            try {
                reg.unregister();
            } catch (IllegalStateException e) {
                // Ignore
            }
        }
        weld.shutdown();
        if (!set) {
            BundleSingletonProvider.currentBundle.remove();
        }
    }

    public Event event() {
        return weld.getInstance().select(Event.class).get();
    }

    public BeanManager beanManager() {
        return weld.getBeanManager();
    }

    public Instance<Object> instance() {
        return weld.getInstance();
    }
}
