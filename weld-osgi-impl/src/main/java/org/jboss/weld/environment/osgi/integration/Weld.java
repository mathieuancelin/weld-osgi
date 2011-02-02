package org.jboss.weld.environment.osgi.integration;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.jboss.weld.bootstrap.WeldBootstrap;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerShutdown;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.BundleBeanDeploymentArchiveFactory;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.BundleDeployment;
import org.jboss.weld.manager.api.WeldManager;
import org.osgi.framework.Bundle;

public class Weld {

    private final static Logger LOGGER = Logger.getLogger(Weld.class.getName());
    private final Bundle bundle;
    private BundleDeployment deployment;
    private boolean started = false;
    private Bootstrap bootstrap;
    private boolean hasShutdownBeenCalled = false;
    private BundleBeanDeploymentArchiveFactory factory;
    private WeldManager manager;

    public Weld(Bundle bundle) {
        this.bundle = bundle;
        factory = new BundleBeanDeploymentArchiveFactory();
    }

    public boolean isStarted() {
        return started;
    }

    /**
     * Boots Weld and creates and returns a CDIContainerImpl instance, through which
     * beans and events can be accessed.
     */
    public boolean initialize() {
        started = false;
        try {
            Enumeration beansXml = bundle.findEntries("META-INF", "beans.xml", true);
            if (beansXml == null) {
                return started;
            }
            System.out.println("Starting Weld container for bundle " + bundle.getSymbolicName());
            bootstrap = new WeldBootstrap();
            deployment = createDeployment(bootstrap);
            // Set up the container
            bootstrap.startContainer(Environments.SE, deployment);
            // Start the container
            bootstrap.startInitialization();
            bootstrap.deployBeans();
            bootstrap.validateBeans();
            bootstrap.endInitialization();

            // Get this Bundle BeanManager
            manager = bootstrap.getManager(deployment.getBeanDeploymentArchive());
            manager.fireEvent(new BundleContainerInitialized(bundle.getBundleContext()));

            started = true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return started;
    }

    private BundleDeployment createDeployment(Bootstrap bootstrap) {
        return new BundleDeployment(bundle, bootstrap, factory);
    }

    public void shutdown() {
        // TODO this should also be part of the extension ...
        if (started) {
            synchronized (this) {
                if (!hasShutdownBeenCalled) {
                    System.out.println("Stopping Weld container for bundle " + bundle.getSymbolicName());
                    hasShutdownBeenCalled = true;
                    try {
                        manager.fireEvent(new BundleContainerShutdown(bundle.getBundleContext()));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    try {
                        bootstrap.shutdown();
                    } catch (Throwable t) {}
                    started = false;
                } else {
                    LOGGER.log(Level.INFO, "Skipping spurious call to shutdown");
                }
            }
        }
    }

    public Event getEvent() {
        return manager.instance().select(Event.class).get();
    }

    public BeanManager getBeanManager() {
        return manager;
    }

    public Instance<Object> getInstance() {
        return manager.instance();
    }
}
