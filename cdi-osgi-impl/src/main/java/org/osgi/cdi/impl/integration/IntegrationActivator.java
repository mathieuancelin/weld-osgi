package org.osgi.cdi.impl.integration;

import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.services.BundleHolder;
import org.osgi.cdi.impl.extension.services.ContainerObserver;
import org.osgi.cdi.impl.extension.services.RegistrationsHolderImpl;
import org.osgi.cdi.api.extension.events.BundleContainerInitialized;
import org.osgi.cdi.api.extension.events.BundleContainerShutdown;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.api.integration.CDIContainers;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * @author Guillaume Sauthier
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class IntegrationActivator implements BundleActivator, BundleListener, CDIContainers, ServiceListener {

    private Map<Long, CDIContainer> managed;
    private ServiceReference factoryRef = null;
    private BundleContext context;
    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start(BundleContext context) throws Exception {

        managed = new HashMap<Long, CDIContainer>();
        this.context = context;
        ServiceReference[] refs = context.getServiceReferences(CDIContainerFactory.class.getName(), null);
        if (refs != null && refs.length > 0) {
            factoryRef = refs[0];
            startCDIOSGi();
        }
        context.addServiceListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stopCDIOSGi();

    }

    public void startCDIOSGi() throws Exception {
        started.set(true);
        managed = new HashMap<Long, CDIContainer>();
        for (Bundle bundle : context.getBundles()) {
            if (Bundle.ACTIVE == bundle.getState()) {
                startManagement(bundle);
            }
        }
        context.addBundleListener(this);
    }

    public void stopCDIOSGi() throws Exception {
        started.set(false);
        for (Bundle bundle : context.getBundles()) {
            CDIContainer holder = managed.get(bundle.getBundleId());
            if (holder != null) {
                stopManagement(holder.getBundle());
            }
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                if (started.get())
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
        CDIContainer holder = managed.get(bundle.getBundleId());
        if (holder != null) {
            Collection<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                try {
                    reg.unregister();
                } catch (IllegalStateException e) {
                    // Ignore
                }
            }
            try {
                holder.getBeanManager().fireEvent(new BundleContainerShutdown(bundle.getBundleContext()));
                // unregistration for managed services. It should be done by the OSGi framework
                RegistrationsHolderImpl regsHolder = holder.getInstance().select(RegistrationsHolderImpl.class).get();
                for (ServiceRegistration r : regsHolder.getRegistrations()) {
                    try {
                        r.unregister();
                    } catch (Exception e) {
                        // the service is already unregistered if shutdown is called when bundle is stopped
                        // but with a manual boostrap, you can't be sure
                        //System.out.println("Service already unregistered.");
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            holder.shutdown();
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
        CDIContainer holder = ((CDIContainerFactory) context.getService(factoryRef)).container(bundle);
        holder.initialize();
        if (holder.isStarted()) {

            // setting contextual informations
            holder.getInstance().select(BundleHolder.class).get().setBundle(bundle);
            holder.getInstance().select(BundleHolder.class).get().setContext(bundle.getBundleContext());
            holder.getInstance().select(ContainerObserver.class).get().setContainers(this);
            holder.getInstance().select(ContainerObserver.class).get().setCurrentContainer(holder);
            // fire container start
            ServicePublisher publisher = new ServicePublisher(holder.getBeanClasses(),
                bundle, holder.getInstance(),
                ((CDIContainerFactory) context.getService(factoryRef)).getContractBlacklist());
            // registering publishable services
            publisher.registerAndLaunchComponents();
            holder.getBeanManager().fireEvent(new BundleContainerInitialized(bundle.getBundleContext()));
            Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();

            BundleContext bundleContext = bundle.getBundleContext();
            try {
                regs.add(
                        bundleContext.registerService(Event.class.getName(),
                                                      holder.getEvent(),
                                                      null));

                regs.add(
                        bundleContext.registerService(BeanManager.class.getName(),
                                                      holder.getBeanManager(),
                                                      null));

                regs.add(
                        bundleContext.registerService(Instance.class.getName(),
                                                      holder.getInstance(),
                                                      null));
            } catch (Throwable t) {
                // Ignore
            }
            holder.setRegistrations(regs);
            managed.put(bundle.getBundleId(), holder);
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        try {
            ServiceReference[] refs = context.getServiceReferences(CDIContainerFactory.class.getName(), null);
            if (ServiceEvent.REGISTERED == event.getType()) {
                if (!started.get() && refs != null && refs.length > 0) {
                    factoryRef = refs[0];
                    startCDIOSGi();
                }
            } else if (ServiceEvent.UNREGISTERING == event.getType()) {
                if (started.get() && (refs == null || refs.length == 0)) {
                    factoryRef = null;
                    stopCDIOSGi();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Iterator<CDIContainer> iterator() {
        return managed.values().iterator();
    }
}
