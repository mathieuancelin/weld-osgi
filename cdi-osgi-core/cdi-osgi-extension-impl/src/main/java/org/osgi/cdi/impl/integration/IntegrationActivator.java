/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.cdi.impl.integration;

import org.osgi.cdi.api.extension.BundleState;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;
import org.osgi.cdi.api.extension.events.Invalid;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.services.BundleHolder;
import org.osgi.cdi.impl.extension.services.ContainerObserver;
import org.osgi.cdi.impl.extension.services.RegistrationsHolderImpl;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is the activator of the CDI-OSGi extension part. It starts with the extension bundle.
 * <p/>
 * It looks for a CDI container factory service before it starts managing bean bundles.
 * It monitors bundle and service events to manage/unmanage arriving/departing bean bundle and to start/stop when a CDI
 * container factory service arrives/leaves.
 *
 * @author Guillaume Sauthier
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class IntegrationActivator implements BundleActivator, BundleListener, ServiceListener {

    private static Logger logger = LoggerFactory.getLogger(IntegrationActivator.class);

    private ServiceReference factoryRef = null;
    private BundleContext context;
    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        ServiceReference[] refs = context.getServiceReferences(CDIContainerFactory.class.getName(), null);
        if (refs != null && refs.length > 0) {
            factoryRef = refs[0];
            startCDIOSGi();
        } else {
            logger.warn("No CDI container factory service found");
        }
        context.addServiceListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stopCDIOSGi();
    }

    public void startCDIOSGi() throws Exception {
        logger.debug("CDI-OSGi start bundle management");
        started.set(true);
        for (Bundle bundle : context.getBundles()) {
            logger.debug("Scanning for start {}", bundle.getSymbolicName());
            if (Bundle.ACTIVE == bundle.getState()) {
                startManagement(bundle);
            }
        }
        context.addBundleListener(this);
    }

    public void stopCDIOSGi() throws Exception {
        logger.debug("CDI-OSGi stop bundle management");
        started.set(false);
        for (Bundle bundle : context.getBundles()) {
            logger.debug("Scanning for stop {}", bundle.getSymbolicName());
            CDIContainer holder = factory().container(bundle);
            if (holder != null) {
                stopManagement(holder.getBundle());
            }
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                logger.debug("Bundle {} started",  event.getBundle().getSymbolicName());
                if (started.get())
                    startManagement(event.getBundle());
                break;
            case BundleEvent.STOPPED:
                logger.debug("Bundle {} stopped", event.getBundle().getSymbolicName());
                stopManagement(event.getBundle());
                break;
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        try {
            ServiceReference[] refs = context.getServiceReferences(CDIContainerFactory.class.getName(), null);
            if (ServiceEvent.REGISTERED == event.getType()) {
                if (!started.get() && refs != null && refs.length > 0) {
                    logger.debug("CDI container factory service found");
                    factoryRef = refs[0];
                    startCDIOSGi();
                }
            } else if (ServiceEvent.UNREGISTERING == event.getType()) {
                if (started.get() && (refs == null || refs.length == 0)) {
                    logger.warn("CDI container factory service unregistered");
                    factoryRef = null;
                    stopCDIOSGi();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startManagement(Bundle bundle) {
        logger.debug("Managing " + bundle.getSymbolicName());
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        CDIContainer holder = factory().createContainer(bundle);
        logger.debug("CDI container created");
        holder.initialize();
        logger.debug("CDI container initialized");
        if (holder.isStarted()) {
            logger.debug("CDI container started");
            // setting contextual information
            holder.getInstance().select(BundleHolder.class).get().setBundle(bundle);
            holder.getInstance().select(BundleHolder.class).get().setContext(bundle.getBundleContext());
            holder.getInstance().select(ContainerObserver.class).get().setContainers(factory().containers());
            holder.getInstance().select(ContainerObserver.class).get().setCurrentContainer(holder);
            // registering publishable services
            ServicePublisher publisher = new ServicePublisher(holder.getBeanClasses(),
                                                              bundle,
                                                              holder.getInstance(),
                                                              factory().getContractBlacklist());
            publisher.registerAndLaunchComponents();
            // fire container start
            holder.getBeanManager().fireEvent(new BundleContainerEvents.BundleContainerInitialized(bundle.getBundleContext()));
            // registering utility services
            Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();
            BundleContext bundleContext = bundle.getBundleContext();
            try {
                regs.add(bundleContext.registerService(Event.class.getName(), holder.getEvent(), null));
                regs.add(bundleContext.registerService(BeanManager.class.getName(), holder.getBeanManager(), null));
                regs.add(bundleContext.registerService(Instance.class.getName(), holder.getInstance(), null));
            } catch (Throwable t) {// Ignore
                logger.warn("Unable to register a utility service", t);
            }
            holder.setRegistrations(regs);
            factory().addContainer(holder);
            logger.debug("Bundle {} is managed", bundle.getSymbolicName());
        } else {
            logger.debug("Bundle {} is not a bean bundle", bundle.getSymbolicName());
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    private void stopManagement(Bundle bundle) {
        logger.debug("Unmanaging {}", bundle.getSymbolicName());
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        CDIContainer holder = factory().container(bundle);
        if (holder != null) {
            logger.debug("Bundle {} got a CDI container", bundle.getSymbolicName());
            factory().removeContainer(bundle);
            Collection<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                try {
                    reg.unregister();
                } catch (IllegalStateException e) {// Ignore
                    //logger.warn("Unable to unregister a service", e);
                }
            }
            try {
                holder.getBeanManager().fireEvent(new BundleContainerEvents.BundleContainerShutdown(bundle.getBundleContext()));
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
            BundleHolder bundleHolder = holder.getInstance().select(BundleHolder.class).get();
            if (bundleHolder.getState().equals(BundleState.VALID)) {
                bundleHolder.setState(BundleState.INVALID);
                holder.getBeanManager().fireEvent(new Invalid());
            }
            holder.shutdown();
            logger.debug("Bundle {}  is unmanaged", bundle.getSymbolicName());
        } else {
            logger.debug("Bundle {} is not a bean bundle", bundle.getSymbolicName());
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    public CDIContainerFactory factory() {
        return (CDIContainerFactory) context.getService(factoryRef);
    }
}
