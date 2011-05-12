package org.osgi.cdi.api.integration;

import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Collection;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface CDIContainer {

    boolean initialize();

    boolean shutdown();

    boolean isStarted();

    void fire(InterBundleEvent event);

    Bundle getBundle();

    BeanManager getBeanManager();

    Event getEvent();

    Instance<Object> getInstance();

    Collection<String> getBeanClasses();

    Collection<ServiceRegistration> getRegistrations();

    void setRegistrations(Collection<ServiceRegistration> registrations);

}
