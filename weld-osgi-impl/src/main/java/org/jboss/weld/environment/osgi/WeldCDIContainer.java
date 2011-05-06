package org.jboss.weld.environment.osgi;

import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.ExtensionActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Collection;

/**
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldCDIContainer implements CDIContainer {

    private final Bundle bundle;
    private Weld container;
    private Collection<ServiceRegistration> registrations;

    public WeldCDIContainer(Bundle bundle) {
        this.bundle = bundle;
        container = new Weld(bundle);
    }

    @Override
    public void setRegistrations(Collection<ServiceRegistration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public Collection<ServiceRegistration> getRegistrations() {
        return registrations;
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public boolean shutdown() {
        return container.shutdown();
    }

    @Override
    public void fire(InterBundleEvent event) {
        Long set = CDIOSGiExtension.currentBundle.get();
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        container.getEvent().select(InterBundleEvent.class,
                new ExtensionActivator.SpecificationAnnotation(event.type()),
                new ExtensionActivator.SentAnnotation()).fire(event);
        if (set != null) {
            CDIOSGiExtension.currentBundle.set(set);
        } else {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    @Override
    public boolean initialize() {
        return container.initialize();
    }

    @Override
    public boolean isStarted() {
        return container.isStarted();
    }

    @Override
    public Event getEvent() {
        return container.getInstance().select(Event.class).get();
    }

    @Override
    public BeanManager getBeanManager() {
        return container.getBeanManager();
    }

    @Override
    public Instance<Object> getInstance() {
        return container.getInstance();
    }

    @Override
    public Collection<String> getBeanClasses() {
        return container.getBeanClasses();
    }
}