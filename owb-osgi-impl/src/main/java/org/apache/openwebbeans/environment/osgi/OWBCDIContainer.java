package org.apache.openwebbeans.environment.osgi;

import java.util.Collection;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.apache.openwebbeans.environment.osgi.integration.OWB;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainers;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.ExtensionActivator.SentAnnotation;
import org.osgi.cdi.impl.extension.ExtensionActivator.SpecificationAnnotation;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OWBCDIContainer implements CDIContainer {

    private final Bundle bundle;
    private OWB container;
    private Collection<ServiceRegistration> registrations;

    public OWBCDIContainer(Bundle bundle) {
        this.bundle = bundle;
        container = new OWB(bundle);
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
                new SpecificationAnnotation(event.type()),
                new SentAnnotation()).fire(event);
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
