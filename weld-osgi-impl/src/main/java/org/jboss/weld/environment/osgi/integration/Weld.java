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
import org.jboss.weld.environment.osgi.api.extension.events.CDIContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.events.CDIContainerShutdown;
import org.jboss.weld.environment.osgi.api.extension.Publish;
import org.jboss.weld.environment.osgi.api.extension.Startable;
import org.jboss.weld.environment.osgi.api.integration.CDIOSGiContainer;
import org.jboss.weld.environment.osgi.extension.context.BundleContext;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.BundleBeanDeploymentArchiveFactory;
import org.jboss.weld.environment.osgi.integration.discovery.bundle.WeldOSGiBundleDeployment;
import org.osgi.framework.Bundle;

public class Weld implements CDIOSGiContainer {

    private final static Logger LOGGER = Logger.getLogger(Weld.class.getName());
    private final Bundle bundle;
    private WeldOSGiBundleDeployment deployment;
    private CDIContainerImpl container;
    private boolean started = false;
    private Bootstrap bootstrap;
    private boolean hasShutdownBeenCalled = false;
    private BundleBeanDeploymentArchiveFactory factory;

    public Weld(Bundle bundle) {
        this.bundle = bundle;
        factory = new BundleBeanDeploymentArchiveFactory();
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
        try {
            Enumeration beansXml = bundle.findEntries("META-INF", "beans.xml", true);
            if (beansXml == null) {
                return started;
            }
            System.out.println("Starting Weld container for bundle " + bundle.getSymbolicName());
            bootstrap = (Bootstrap) new WeldBootstrap();
            deployment = createDeployment(bootstrap);
            // Set up the container
            bootstrap.startContainer(Environments.SE, deployment);
            // Start the container
            bootstrap.startInitialization();
            bootstrap.deployBeans();
            bootstrap.validateBeans();
            bootstrap.endInitialization();
            container = getInstanceByType(bootstrap.getManager(deployment.loadBeanDeploymentArchive(CDIContainerImpl.class)), CDIContainerImpl.class);
            container.event().select(CDIContainerInitialized.class).fire(new CDIContainerInitialized());

            // TODO Move this in extension ...
            System.out.println(String.format("\nRegistering/Starting OSGi Service for bundle %s\n", bundle.getSymbolicName()));
            registerAndLaunchComponents();
            started = true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return started;
    }

    private void registerAndLaunchComponents() {
        // TODO all of this should be part of the extension ....
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
                    if (publishable) {
                        // register service
                        if (service != null) {
                            Publish publish = clazz.getAnnotation(Publish.class);
                            Class[] contracts = publish.contracts();
                            if (contracts.length != 0) {
                                for (Class contract : contracts) {
                                    System.out.println("Registering OSGi service " + clazz.getName() + " as " + contract.getName());
                                    bundle.getBundleContext().registerService(contract.getName(), service, null);
                                }
                            } else {
                                // registering interfaces
                                if (service.getClass().getInterfaces().length > 0) {
                                    for (Class interf : service.getClass().getInterfaces()) {
                                        // TODO : Beurk !!!!!!!!!!!!!, there must me some kind of helper somewhere
                                        if (!interf.getName().equals("java.io.Serializable") &&
                                            !interf.getName().equals("org.jboss.interceptor.proxy.LifecycleMixin") &&
                                            !interf.getName().equals("org.jboss.interceptor.util.proxy.TargetInstanceProxy") &&
                                            !interf.getName().equals("javassist.util.proxy.ProxyObject")) {
                                        System.out.println("Registering OSGi service " + clazz.getName() + " as " + interf.getName());
                                        bundle.getBundleContext().registerService(interf.getName(), service, null);
                                    }   }
                                } else {
                                    System.out.println("Registering OSGi service " + clazz.getName() +  " as " + clazz.getName());
                                    bundle.getBundleContext().registerService(clazz.getName(), service, null);
                                }
                            }
                        }
                    }
                    if (service != null && startable) {
                        System.out.println("Starting " + className);
                        service.toString(); // TODO : Okay, it's really ugly, but I have no choice with these lazy proxies
                    }
                }
            }
        }
    }

    @Override
    public CDIContainerImpl getContainer() {
        return container;
    }

    private WeldOSGiBundleDeployment createDeployment(Bootstrap bootstrap) {
        return new WeldOSGiBundleDeployment(bundle, bootstrap, factory);
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
        // TODO this should also be part of the extension ...
        if (started) {
            synchronized (this) {
                if (!hasShutdownBeenCalled) {
                    System.out.println("Stopping Weld container for bundle " + bundle.getSymbolicName());
                    hasShutdownBeenCalled = true;
                    try {
                        container.event().select(CDIContainerShutdown.class).fire(new CDIContainerShutdown());
                    } catch (Throwable t) {
                        // Ignore
                    }
                    BundleContext.invalidateBundle(bundle);
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
}
