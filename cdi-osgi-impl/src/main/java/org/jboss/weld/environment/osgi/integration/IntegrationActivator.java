package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.environment.osgi.extension.CDIOSGiExtension;
import org.jboss.weld.environment.osgi.extension.ExtensionActivator.SentAnnotation;
import org.jboss.weld.environment.osgi.extension.ExtensionActivator.SpecificationAnnotation;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.BundleContainer;
import org.osgi.cdi.api.integration.BundleContainers;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 27/01/11
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationActivator implements BundleActivator, BundleListener, BundleContainers {

    private Map<Long, BundleContainer> managed;

    @Override
    public void start(BundleContext context) throws Exception {

        // Init the SingletonProvider
        SingletonProvider.initialize(new BundleSingletonProvider());

        managed = new HashMap<Long, BundleContainer>();

        for (Bundle bundle : context.getBundles()) {
            if (Bundle.ACTIVE == bundle.getState()) {
                startManagement(bundle);
            }
        }

        context.addBundleListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        for (Bundle bundle : context.getBundles()) {
            Holder holder = (Holder) managed.get(bundle.getBundleId());
            if (holder != null) {
                stopManagement(holder.bundle);
            }
        }

        SingletonProvider.reset();
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                startManagement(event.getBundle());
                break;
            case BundleEvent.STOPPED:
                stopManagement(event.getBundle());
                break;
        }
    }

    private void stopManagement(Bundle bundle) {
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        Holder holder = (Holder) managed.get(bundle.getBundleId());
        if (holder != null) {
            Collection<ServiceRegistration> regs = holder.registrations;
            for (ServiceRegistration reg : regs) {
                try {
                    reg.unregister();
                } catch (IllegalStateException e) {
                    // Ignore
                }
            }
            holder.container.shutdown();
            managed.remove(bundle.getBundleId());
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    private void startManagement(Bundle bundle) {
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        //System.out.println("Starting management for bundle " + bundle);
        Holder holder = new Holder();
        Weld weld = new Weld(bundle);
        weld.initialize(holder, this);

        if (weld.isStarted()) {
            
            Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();

            BundleContext bundleContext = bundle.getBundleContext();
            try {
                regs.add(
                        bundleContext.registerService(Event.class.getName(),
                                              weld.getEvent(),
                                              null));

                regs.add(
                        bundleContext.registerService(BeanManager.class.getName(),
                                weld.getBeanManager(),
                                null));

                regs.add(
                        bundleContext.registerService(Instance.class.getName(),
                                weld.getInstance(),
                                null));
            } catch (Throwable t) {
                // Ignore
            }
            holder.container = weld;
            holder.registrations = regs;
            holder.bundle = bundle;
            managed.put(bundle.getBundleId(), holder);
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    @Override
    public Collection<BundleContainer> getContainers() {
        return managed.values();
    }

    private static class Holder implements BundleContainer {
        Bundle bundle;
        Weld container;
        Collection<ServiceRegistration> registrations;

        @Override
        public void fire(InterBundleEvent event) {
            Long set = CDIOSGiExtension.currentBundle.get();
            CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
            container.getEvent().select(InterBundleEvent.class,
                    new SpecificationAnnotation(event.type()),
                    new SentAnnotation()).fire(event);
            if (set != null) {
                CDIOSGiExtension.currentBundle.set(set);
            } else {
                CDIOSGiExtension.currentBundle.remove();
            }
        }
    }
}
