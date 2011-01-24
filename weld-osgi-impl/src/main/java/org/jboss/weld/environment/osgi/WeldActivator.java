package org.jboss.weld.environment.osgi;

import java.util.concurrent.ConcurrentHashMap;
import org.jboss.weld.environment.osgi.api.WeldOSGiContainer;
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
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldActivator implements BundleActivator,
                                      BundleListener,
                                      WeldOSGiContainer {

    private final ConcurrentHashMap<Long, Weld> containers;

    public WeldActivator() {
        containers = new ConcurrentHashMap<Long, Weld>();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(WeldOSGiContainer.class.getName(), this, null);
        context.addBundleListener(this);

        for (Bundle bundle : context.getBundles()) {
            if (!containers.containsKey(bundle.getBundleId())) {
                if (bundle.getState() >= Bundle.STARTING) {
                    Weld container = new Weld(bundle);
                    containers.putIfAbsent(bundle.getBundleId(), container);
                }
            }
        }
        for (Weld container : containers.values()) {
            boolean started = container.initialize();
            if (!started) {
                containers.remove(context.getBundle().getBundleId());
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        for (Weld container : containers.values()) {
            container.shutdown();
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        
        if (event.getType() == BundleEvent.STARTED) {
            if (!containers.containsKey(event.getBundle().getBundleId())) {
                Weld container =  new Weld(event.getBundle());
                Weld cont = containers.putIfAbsent(
                        event.getBundle().getBundleId(), container);
                if (cont == null) {
                    Weld weld = containers.get(event.getBundle().getBundleId());
                    boolean started = weld.initialize();
                    if (!started) {
                        containers.remove(event.getBundle().getBundleId());
                    }
                }
            }
        }
        if (BundleEvent.STOPPED == event.getType()) {
            if (containers.containsKey(event.getBundle().getBundleId())) {
                Weld container = containers.get(event.getBundle().getBundleId());
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

//    @Override
//    public WeldContainer getContainer(Bundle bundle) {
//        if (containers.containsKey(bundle.getBundleId())) {
//            return containers.get(bundle.getBundleId()).getContainer();
//        } else {
//            throw new RuntimeException("No container attached to the current bundle.");
//        }
//    }
}
