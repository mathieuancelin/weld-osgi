package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainer;
import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainerFactory;
import org.osgi.framework.*;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.*;
import org.jboss.weld.environment.osgi.extension.context.BundleSingletonProvider;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 27/01/11
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationActivator implements BundleActivator, BundleListener {


    private WeldFactory factory;

    private Map<Long, Holder> managed;

    @Override
    public void start(BundleContext context) throws Exception {

        // Init the SingletonProvider
        SingletonProvider.initialize(new BundleSingletonProvider());

        managed = new HashMap<Long, Holder>();

        factory = new WeldFactory();
        context.registerService(CDIOSGiContainerFactory.class.getName(),
                factory,
                null);

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
            Holder holder = managed.get(bundle.getBundleId());
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
        boolean set = BundleSingletonProvider.currentBundle.get() != null;
        BundleSingletonProvider.currentBundle.set(bundle.getBundleId());
        Holder holder = managed.get(bundle.getBundleId());
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
        if (!set)
            BundleSingletonProvider.currentBundle.remove();
    }

    private void startManagement(Bundle bundle) {
        boolean set = BundleSingletonProvider.currentBundle.get() != null;
        BundleSingletonProvider.currentBundle.set(bundle.getBundleId());
        //System.out.println("Starting management for bundle " + bundle);
        CDIOSGiContainer container = factory.getContainer(bundle);
        container.initialize();

        if (container.isStarted()) {
            
            Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();

            BundleContext bundleContext = bundle.getBundleContext();
            try {
                regs.add(
                        bundleContext.registerService(Event.class.getName(),
                                              container.getContainer().event(),
                                              null));

                regs.add(
                        bundleContext.registerService(BeanManager.class.getName(),
                                container.getContainer().getBeanManager(),
                                null));

                regs.add(
                        bundleContext.registerService(Instance.class.getName(),
                                container.getContainer().instance(),
                                null));
            } catch (Throwable t) {
                // Ignore
            }
            Holder holder = new Holder();
            holder.container = container;
            holder.registrations = regs;
            holder.bundle = bundle;
            managed.put(bundle.getBundleId(), holder);
        }
        if (!set)
            BundleSingletonProvider.currentBundle.remove();
    }

    private static class Holder {
        Bundle bundle;
        CDIOSGiContainer container;
        Collection<ServiceRegistration> registrations;
    }
}
