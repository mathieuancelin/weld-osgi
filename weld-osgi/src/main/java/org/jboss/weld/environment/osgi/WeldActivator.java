package org.jboss.weld.environment.osgi;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.weld.environment.osgi.discovery.bundle.WeldOSGiBundleDeployment;
import org.jboss.weld.environment.osgi.events.ContainerInitialized;
import org.jboss.weld.environment.osgi.events.ContainerShutdown;
import org.jboss.weld.environment.osgi.integration.Publish;
import org.jboss.weld.environment.osgi.integration.Startable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 * Entry point of the OSGi Bundle. Start the Weld container et listen to bundle
 * installation.
 *
 * @author Mathieu ANCELIN
 */
public class WeldActivator implements BundleActivator, BundleListener
        , ServiceListener, WeldContainerFetcher {

    private ConcurrentHashMap<Long, BundleWeldContainer> containers;

    public WeldActivator() {
        containers = new ConcurrentHashMap<Long, BundleWeldContainer>();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(WeldContainerFetcher.class.getName(), this, null);
        context.addBundleListener(this);
        context.addServiceListener(this);
        for (Bundle bundle : context.getBundles()) {
            if (!containers.containsKey(bundle.getBundleId())) {
                if (bundle.getState() >= Bundle.RESOLVED) {
                    BundleWeldContainer container = new BundleWeldContainer(bundle);
                    containers.putIfAbsent(bundle.getBundleId(), container);
                }
            }
        }
        for (BundleWeldContainer container : containers.values()) {
            container.start();
            container.registerAndLaunchComponents();
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        for (BundleWeldContainer container : containers.values()) {
            container.stop();
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        for (BundleWeldContainer container : containers.values()) {
            container.fire(event);
        }
        if (BundleEvent.RESOLVED == event.getType()) {
            if (!containers.containsKey(event.getBundle().getBundleId())) {
                BundleWeldContainer container =  new BundleWeldContainer(event.getBundle());
                BundleWeldContainer cont = containers.putIfAbsent(
                        event.getBundle().getBundleId(), container);
                if (cont == null) {
                    containers.get(event.getBundle().getBundleId()).start();
                    containers.get(event.getBundle().getBundleId()).registerAndLaunchComponents();
                }
            }
        }
        if (BundleEvent.UNRESOLVED == event.getType()) {
            if (containers.containsKey(event.getBundle().getBundleId())) {
                containers.remove(event.getBundle().getBundleId());
            }
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        for (BundleWeldContainer container : containers.values()) {
            container.fire(event);
        }
    }

    @Override
    public WeldContainer getContainer(Bundle bundle) {
        if (containers.containsKey(bundle.getBundleId())) {
            return containers.get(bundle.getBundleId()).getContainer();
        } else {
            throw new RuntimeException("No container attached to the current bundle.");
        }
    }

    private class BundleWeldContainer {
        
        private ShutdownManager manager;
        private final Bundle bundle;
        private WeldContainer container;
        private Weld weld;
        private boolean started = false;

        public BundleWeldContainer(Bundle bundle) {
            this.bundle = bundle;
        }

        public BundleWeldContainer start() {
            if (!started) {
                weld = new Weld();
                container = weld.initialize(bundle);
                container.event().select(ContainerInitialized.class).fire(new ContainerInitialized());
                manager = container.instance().select(ShutdownManager.class).get();
                started = true;
                return this;
            }
            return this;
        }

        public void registerAndLaunchComponents() {
            if (started) {
                WeldOSGiBundleDeployment deployment = weld.getDeployment();
                Collection<String> classes = deployment.getBeanDeploymentArchive().getBeanClasses();
                for (String className : classes) {
                    Class<?> clazz = null;
                    try {
                        clazz = getClass().getClassLoader().loadClass(className);
                    } catch (Exception e) {
                        // e.printStackTrace(); // silently ignore :-)
                    }
                    if (clazz != null) {
                        boolean publishable = clazz.isAnnotationPresent(Publish.class);
                        boolean startable = clazz.isAnnotationPresent(Startable.class);
                        boolean instatiation = publishable | startable;
                        Object service = null;
                        if (instatiation) {
                            // instanciation, so component is starting (@PostConstruct)
                            try {
                                System.out.println("instaciate " + clazz);
                                service = container.instance().select(clazz).get();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                        if (publishable) {
                            // register service
                            if (service != null) {
                                bundle.getBundleContext().registerService(className, service, null);
                            }
                        }
                    }
                }
            }
        }

        public void stop() {
            if (started) {
                container.event().fire(new ContainerShutdown());
                manager.shutdown();
            }
        }

        public void fire(Object event) {
            if (started) {
                container.event().fire(event);
            }
        }

        public WeldContainer getContainer() {
            return container;
        }
    }
}
