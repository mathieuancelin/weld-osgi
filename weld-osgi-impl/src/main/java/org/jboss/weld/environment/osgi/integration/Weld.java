package org.jboss.weld.environment.osgi.integration;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Qualifier;
import org.jboss.weld.bootstrap.WeldBootstrap;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.environment.osgi.OSGiEnvironment;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerShutdown;
import org.jboss.weld.environment.osgi.api.extension.Publish;
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
            // ugly hack to make jboss interceptors works.
            // thank you Thread.currentThread().getContextClassLoader().loadClass()
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            // -------------
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
            manager.fireEvent(new BundleContainerInitialized(bundle.getBundleContext()));
            
            // TODO Move this in extension ...
            System.out.println(String.format("\nRegistering/Starting OSGi Service for bundle %s\n", bundle.getSymbolicName()));
            registerAndLaunchComponents();
            Thread.currentThread().setContextClassLoader(old);
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
                clazz = bundle.loadClass(className);//bundle.loadClass(className);
                //clazz = resourceLoader.classForName(className);
            } catch (Exception e) {
                //e.printStackTrace(); // silently ignore :-)
            }
            if (clazz != null) {
                boolean publishable = clazz.isAnnotationPresent(Publish.class);
                boolean instatiation = publishable;
                Annotation[] annotations = null;
                Object service = null;
                if (instatiation) {
                    try {
                        List<Annotation> qualifiers = new ArrayList<Annotation>();
                        for (Annotation a : clazz.getAnnotations()) {
                            if (a.annotationType().isAnnotationPresent(Qualifier.class)) {
                                qualifiers.add(a);
                            }
                        }
                        
                        annotations = qualifiers.toArray(new Annotation[qualifiers.size()]);
                        service = manager.instance().select(clazz, annotations).get();
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
                                    bundle.getBundleContext().registerService(
                                            contract.getName(), getProxy(contract, annotations, bundle), null);
                                }
                            } else {
                                // registering interfaces
                                if (service.getClass().getInterfaces().length > 0) {
                                    for (Class interf : service.getClass().getInterfaces()) {
                                        // TODO : Beurk !!!!!!!!!!!!!, there must be some kind of helper somewhere
                                        if (!interf.getName().equals("java.io.Serializable") &&
                                            !interf.getName().equals("org.jboss.interceptor.proxy.LifecycleMixin") &&
                                            !interf.getName().equals("org.jboss.interceptor.util.proxy.TargetInstanceProxy") &&
                                            !interf.getName().equals("javassist.util.proxy.ProxyObject")) {
                                                System.out.println("Registering OSGi service " + clazz.getName() + " as " + interf.getName());
                                                bundle.getBundleContext().registerService(
                                                        interf.getName(), getProxy(interf, annotations, bundle), null);
                                        }
                                    }
                                } else {
                                    System.out.println("Registering OSGi service " + clazz.getName() +  " as " + clazz.getName());
                                    bundle.getBundleContext().registerService(clazz.getName(), service, null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private <T> T getProxy(Class<T> clazz, Annotation[] qualifiers, Bundle bundle) {
        return clazz.cast(
            Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[] {clazz},
                new LazyService(clazz, qualifiers, bundle)
            )
        );
    }
    
    private class LazyService implements InvocationHandler {

        private final Class<?> contract;
        private final Annotation[] qualifiers;
        private final Bundle bundle;

        public LazyService(Class<?> contract, Annotation[] qualifiers, Bundle bundle) {
            this.contract = contract;
            this.qualifiers = qualifiers;
            this.bundle = bundle;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                BundleSingletonProvider.currentBundle.set(bundle.getBundleId());
                return method.invoke(
                    manager.instance().select(contract, qualifiers).get(),
                    args
                );
            } finally {
                BundleSingletonProvider.currentBundle.remove();
            }
        }
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
