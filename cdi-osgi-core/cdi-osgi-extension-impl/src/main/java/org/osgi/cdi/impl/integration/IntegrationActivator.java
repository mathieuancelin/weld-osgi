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

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

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

    private static Logger logger = Logger.getLogger("CDI-OSGi");

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
            logger.warning("No CDI container factory service found");
        }
        context.addServiceListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stopCDIOSGi();
    }

    public void startCDIOSGi() throws Exception {
        logger.info("CDI-OSGi start bundle management");
        started.set(true);
        for (Bundle bundle : context.getBundles()) {
            logger.finest("Scanning " + bundle.getSymbolicName());
            if (Bundle.ACTIVE == bundle.getState()) {
                startManagement(bundle);
            }
        }
        context.addBundleListener(this);
    }

    public void stopCDIOSGi() throws Exception {
        logger.info("CDI-OSGi stop bundle management");
        started.set(false);
        for (Bundle bundle : context.getBundles()) {
            logger.finest("Scanning " + bundle.getSymbolicName());
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
                logger.finest("Bundle " + event.getBundle().getSymbolicName() + " started");
                if (started.get())
                    startManagement(event.getBundle());
                break;
            case BundleEvent.STOPPED:
                logger.finest("Bundle " + event.getBundle().getSymbolicName() + " stopped");
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
                    logger.info("CDI container factory service found");
                    factoryRef = refs[0];
                    startCDIOSGi();
                }
            } else if (ServiceEvent.UNREGISTERING == event.getType()) {
                if (started.get() && (refs == null || refs.length == 0)) {
                    logger.warning("CDI container factory service unregistered");
                    factoryRef = null;
                    stopCDIOSGi();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startManagement(Bundle bundle) {
        logger.fine("Managing " + bundle.getSymbolicName());
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        CDIContainer holder = factory().createContainer(bundle);
        logger.finest("CDI container created");
        holder.initialize();
        logger.finest("CDI container initialized");
        if (holder.isStarted()) {
            logger.finest("CDI container started");
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
                logger.warning("Unable to register a utility service" + t.getCause());
            }
            holder.setRegistrations(regs);
            factory().addContainer(holder);
            logger.fine("Bundle " + bundle.getSymbolicName() + " is managed");
        } else {
            logger.fine("Bundle " + bundle.getSymbolicName() + " is not a bean bundle");
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    private void stopManagement(Bundle bundle) {
        logger.fine("Unmanaging " + bundle.getSymbolicName());
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        CDIContainer holder = factory().container(bundle);
        if (holder != null) {
            logger.finest("Bundle " + bundle.getSymbolicName() + "got a CDI container");
            factory().removeContainer(bundle);
            Collection<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                try {
                    reg.unregister();
                } catch (IllegalStateException e) {// Ignore
                    logger.warning("Unable to unregister a service" + e.getCause());
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
            logger.fine("Bundle " + bundle.getSymbolicName() + " is unmanaged");
        } else {
            logger.fine("Bundle " + bundle.getSymbolicName() + " is not a bean bundle");
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    public CDIContainerFactory factory() {
        return (CDIContainerFactory) context.getService(factoryRef);
    }
}
