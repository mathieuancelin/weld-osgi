package org.osgi.cdi.api.integration;

import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Collection;

/**
 * <p>This interface represents an iterable list of CDI containers used by
 * CDI-OSGi.</p>
 * <p>It allows to: <ul>
 * <li>
 * <p>Navigate through the list of CDI containers as an
 * {@link Iterable},</p>
 * </li>
 * <li>
 * <p>Obtain the number of CDI containers,</p>
 * </li>
 * <li>
 * <p>Select a specific container by its bundle,</p>
 * </li>
 * <li>
 * <p>Start and stop the selected CDI container,</p>
 * </li>
 * <li>
 * <p>Obtain the state of the selected CDI container,</p>
 * </li>
 * <li>
 * <p>Obtain the corresponding {@link Bundle},
 * {@link BeanManager}, {@link Event}, managed bean
 * {@link Class} and {@link Instance} and registred
 * service as {@link ServiceRegistration},</p>
 * </li>
 * <li>
 * <p>Fire {@link InterBundleEvent}.</p>
 * </li>
 * </ul></p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see CDIContainerFactory
 * @see Iterable
 * @see org.osgi.framework.Bundle
 * @see BeanManager
 * @see Instance
 * @see Event
 * @see ServiceRegistration
 * @see InterBundleEvent
 */
public interface CDIContainer {

    /**
     * Initialize the CDI container.
     * @return true if the CDI container is initialized, false if anything goes wrong.
     */
    boolean initialize();

    /**
     * Shutdown the CDI container.
     * @return true if the CDI container is off, false if anything goes wrong.
     */
    boolean shutdown();

    /**
     * Test if the CDI container is on and initialized.
     * @return true if the CDI container is started, false otherwise.
     */
    boolean isStarted();

    /**
     * Fire an {@link InterBundleEvent} from the {@link Bundle} of this {@link CDIContainer}.
     * @param event the {@link InterBundleEvent} to fire.
     */
    void fire(InterBundleEvent event);

    /**
     * Obtain the {@link Bundle} corresponding to this {@link CDIContainer}.
     * @return the {@link Bundle} corresponding to this {@link CDIContainer}.
     */
    Bundle getBundle();

    /**
     * Obtain the {@link BeanManager} of this {@link CDIContainer}.
     * @return the {@link BeanManager} of this {@link CDIContainer}.
     */
    BeanManager getBeanManager();

    /**
     * Obtain the {@link Event} of this {@link CDIContainer}.
     * @return the {@link Event} of this {@link CDIContainer}.
     */
    Event getEvent();

    /**
     * Obtain the managed bean {@link Instance} of this {@link CDIContainer}.
     * @return the managed bean {@link Instance} of this {@link CDIContainer}.
     */
    Instance<Object> getInstance();

    /**
     * Obtain the managed bean class of this {@link CDIContainer}.
     * @return the managed bean class of this {@link CDIContainer} as a {@link Collection} of {@link String}.
     */
    Collection<String> getBeanClasses();

    /**
     * Obtain the {@link ServiceRegistration}s of this {@link CDIContainer}.
     * @return the {@link ServiceRegistration}s of this {@link CDIContainer} as a {@link Collection}.
     */
    Collection<ServiceRegistration> getRegistrations();

    /**
     * Set the {@link ServiceRegistration}s for this {@link CDIContainer}.
     * @param registrations the {@link ServiceRegistration}s for this {@link CDIContainer} as a {@link Collection}.
     */
    void setRegistrations(Collection<ServiceRegistration> registrations);

}
