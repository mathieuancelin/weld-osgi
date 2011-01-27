package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.environment.osgi.extension.CDIContainerImpl;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Qualifier;
import org.jboss.weld.bootstrap.WeldBootstrap;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.environment.osgi.api.extension.CDIContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.CDIContainerShutdown;
import org.jboss.weld.environment.osgi.api.extension.Publish;
import org.jboss.weld.environment.osgi.api.extension.Startable;
import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainer;
import org.jboss.weld.environment.osgi.extension.services.BundleContext;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.WeldOSGiResourceLoader;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.WeldOSGiBundleDeployment;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.osgi.framework.Bundle;

public class Weld implements CDIOSGiContainer {

    private final static Logger LOGGER = Logger.getLogger(Weld.class.getName());
    private final Bundle bundle;
    private WeldOSGiBundleDeployment deployment;
    private CDIContainerImpl container;
    private boolean started = false;
    private Bootstrap bootstrap;
    private boolean hasShutdownBeenCalled = false;
    private ResourceLoader resourceLoader;

    public Weld(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    /**
     * Boots Weld and creates and returns a CDIContainerImpl instance, through which
     * beans and events can be accessed.
     */
    @Override
    public boolean initialize() {
        started = false;
        Enumeration beansXml = bundle.findEntries("META-INF", "beans.xml", true);
        if (beansXml == null) {
            return started;
        }
        resourceLoader = new WeldOSGiResourceLoader(bundle);
        bootstrap = (Bootstrap) new WeldBootstrap();
        deployment = createDeployment(resourceLoader, bootstrap);
        // Set up the container
        bootstrap.startContainer(Environments.SE, deployment);
        // Start the container
        bootstrap.startInitialization();
        bootstrap.deployBeans();
        bootstrap.validateBeans();
        bootstrap.endInitialization();
        container = getInstanceByType(bootstrap.getManager(deployment.loadBeanDeploymentArchive(CDIContainerImpl.class)), CDIContainerImpl.class);
        container.event().select(CDIContainerInitialized.class).fire(new CDIContainerInitialized());
        LOGGER.log(Level.INFO, "Registering/Starting OSGi Service for bundle {0}", bundle.getSymbolicName());
        registerAndLaunchComponents();
        started = true;
        return started;
    }

    private void registerAndLaunchComponents() {
        //if (started) {
        Collection<String> classes = deployment.getBeanDeploymentArchive().getBeanClasses();
        for (String className : classes) {
            Class<?> clazz = null;
            try {
                clazz = bundle.loadClass(className);
                //clazz = resourceLoader.classForName(className);
            } catch (Exception e) {
                //e.printStackTrace(); // silently ignore :-)
            }
            if (clazz != null) {
                boolean publishable = clazz.isAnnotationPresent(Publish.class);
                boolean startable = clazz.isAnnotationPresent(Startable.class);
                boolean instatiation = publishable | startable;
                Object service = null;
                if (instatiation) {
                    try {
                        List<Annotation> qualifiers = new ArrayList<Annotation>();
                        for (Annotation a : clazz.getAnnotations()) {
                            if (a.annotationType().isAnnotationPresent(Qualifier.class)) {
                                qualifiers.add(a);
                            }
                        }
                        Annotation[] annotations = new Annotation[qualifiers.size()];
                        service = container.instance().select(clazz, annotations).get();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    if (service != null) {
                        service.toString(); // TODO : Okay, it's really ugly, but I have no choice with these lazy proxies
                    }
                }
                if (publishable) {
                    // register service
                    if (service != null) {
                        bundle.getBundleContext().registerService(clazz.getName(), service, null);
                    }
                }
            }
        }
        // }
    }

    @Override
    public CDIContainerImpl getContainer() {
        return container;
    }

    private WeldOSGiBundleDeployment createDeployment(ResourceLoader resourceLoader, Bootstrap bootstrap) {
        return new WeldOSGiBundleDeployment(bundle, resourceLoader, bootstrap);
    }

    private <T> T getInstanceByType(BeanManager manager, Class<T> type, Annotation... bindings) {
        final Bean<?> bean = manager.resolve(manager.getBeans(type));
        if (bean == null) {
            throw new UnsatisfiedResolutionException("Unable to resolve a bean for " + type + " with bindings " + Arrays.asList(bindings));
        }
        CreationalContext<?> cc = manager.createCreationalContext(bean);
        return type.cast(manager.getReference(bean, type, cc));
    }

    @Override
    public void shutdown() {
        if (started) {
            synchronized (this) {
                if (!hasShutdownBeenCalled) {
                    hasShutdownBeenCalled = true;
                    container.event().select(CDIContainerShutdown.class).fire(new CDIContainerShutdown());
                    BundleContext.invalidateBundle(bundle);
                    bootstrap.shutdown();
                    started = false;
                } else {
                    LOGGER.log(Level.INFO, "Skipping spurious call to shutdown");
                }
            }
        }
    }
}
