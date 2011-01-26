package org.jboss.weld.environment.osgi.extension;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainer;
import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainerFactory;
import org.jboss.weld.environment.osgi.integration.WeldFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * Entry point of the OSGi Bundle. Start the Weld container et listen to bundle
 * installation.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class CDIActivator implements BundleActivator, BundleListener
        , ServiceListener {

    private final ConcurrentHashMap<Long, CDIOSGiContainer> containers;
    private CDIOSGiContainerFactory factory;

    public CDIActivator() {
        containers = new ConcurrentHashMap<Long, CDIOSGiContainer>();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        // TODO : need to find something better
        ServiceReference ref = context.getServiceReference(CDIOSGiContainerFactory.class.getName());
        if (ref == null) {
            factory = new WeldFactory(); // TODO : get ride of this direct instanciation
        } else {
            factory = (CDIOSGiContainerFactory) context.getService(ref);
        }
        context.addBundleListener(this);
        context.addServiceListener(this);
        for (Bundle bundle : context.getBundles()) {
            if (!containers.containsKey(bundle.getBundleId())) {
                if (bundle.getState() >= Bundle.STARTING) {
                    CDIOSGiContainer container = factory.getContainer(bundle);
                    containers.putIfAbsent(bundle.getBundleId(), container);
                }
            }
        }
        for (CDIOSGiContainer container : containers.values()) {
            boolean started = container.initialize();
            if (!started) {
                containers.remove(context.getBundle().getBundleId());
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        for (CDIOSGiContainer container : containers.values()) {
            container.shutdown();
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        
        if (event.getType() == BundleEvent.STARTED) {
            if (!containers.containsKey(event.getBundle().getBundleId())) {
                CDIOSGiContainer container =  factory.getContainer(event.getBundle());
                CDIOSGiContainer maybeExistingContainer = containers.putIfAbsent(
                        event.getBundle().getBundleId(), container);
                if (maybeExistingContainer == null) {
                    CDIOSGiContainer existingContainer = containers.get(event.getBundle().getBundleId());
                    boolean started = existingContainer.initialize();
                    if (!started) {
                        containers.remove(event.getBundle().getBundleId());
                    }
                }
            }
        }
        if (BundleEvent.STOPPED == event.getType()) {
            if (containers.containsKey(event.getBundle().getBundleId())) {
                CDIOSGiContainer container = containers.get(event.getBundle().getBundleId());
                container.shutdown();
                containers.remove(event.getBundle().getBundleId());
            }
        }
//        for (Weld container : containers.values()) {
//            if (container != null) {
//                container.getContainer().event().select(BundleEvent.class).fire(event);
//            }
//        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
//        for (Weld container : containers.values()) {
//            container.getContainer().event().select(ServiceEvent.class).fire(event);
//        }
    }

//    @Override
//    public WeldContainer getContainer(Bundle bundle) {
//        if (containers.containsKey(bundle.getBundleId())) {
//            return containers.get(bundle.getBundleId()).getContainer();
//        } else {
//            throw new RuntimeException("No container attached to the current bundle.");
//        }
//    }
}
