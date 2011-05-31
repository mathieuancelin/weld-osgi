package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.BundleBeanDeploymentArchiveFactory;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.BundleDeployment;
import org.jboss.weld.manager.api.WeldManager;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.framework.Bundle;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class Weld {

    private final static Logger LOGGER = Logger.getLogger(Weld.class.getName());
    private final Bundle bundle;
    private BundleDeployment deployment;
    private boolean started = false;
    private Bootstrap bootstrap;
    private boolean hasShutdownBeenCalled = false;
    private BundleBeanDeploymentArchiveFactory factory;
    private WeldManager manager;
    private Collection<String> beanClasses;

    public Weld(Bundle bundle) {
        this.bundle = bundle;
        factory = new BundleBeanDeploymentArchiveFactory();
    }

    public boolean isStarted() {
        return started;
    }

    /**
     * Boots Weld and creates and returns a CDIContainerImpl instance, through which beans and events can be accessed
     * .
     */
    public boolean initialize() {
        started = false;
        // ugly hack to make jboss interceptors works.
        // thank you Thread.currentThread().getContextClassLoader().loadClass()
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        // -------------
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        try {
            Enumeration beansXml = bundle.findEntries("META-INF", "beans.xml", true);
            if (beansXml == null) {
                return started;
            }
            System.out.println("Starting Weld container for bundle " + bundle.getSymbolicName());
            bootstrap = new WeldBootstrap();
            deployment = createDeployment(bootstrap);
            // Set up the container
            bootstrap.startContainer(new OSGiEnvironment(), deployment);
            // Start the container
            bootstrap.startInitialization();
            bootstrap.deployBeans();
            bootstrap.validateBeans();
            bootstrap.endInitialization();
            // Get this Bundle BeanManager
            manager = bootstrap.getManager(deployment.getBeanDeploymentArchive());
            beanClasses = deployment.getBeanDeploymentArchive().getBeanClasses();
            started = true;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (!set) {
                CDIOSGiExtension.currentBundle.remove();
            }
            Thread.currentThread().setContextClassLoader(old);
        }
        return started;
    }

    private BundleDeployment createDeployment(Bootstrap bootstrap) {
        return new BundleDeployment(bundle, bootstrap, factory);
    }

    public boolean shutdown() {
        if (started) {
            synchronized (this) {
                if (!hasShutdownBeenCalled) {
                    System.out.println("Stopping Weld container for bundle " + bundle.getSymbolicName());
                    hasShutdownBeenCalled = true;
                    try {
                        bootstrap.shutdown();
                    } catch (Throwable t) {
                    }
                    started = false;
                    return true;
                } else {
                    LOGGER.log(Level.INFO, "Skipping spurious call to shutdown");
                    return false;
                }
            }
        }
        return false;
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

    public Collection<String> getBeanClasses() {
        return beanClasses;
    }

}
