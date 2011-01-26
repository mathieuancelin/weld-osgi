package org.jboss.weld.environment.osgi.api.integration;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;

/**
 *
 * @author mathieu
 */
public interface CDIContainer {

    /**
     * Provides access to all events within the application. For example:
     * <code>
     * weld.event().select(Bar.class).fire(new Bar());
     * </code>
     */
    Event<Object> event();

    /**
     * Provides direct access to the BeanManager.
     */
    BeanManager getBeanManager();

    /**
     * Provides access to all beans within the application. For example:
     * <code>
     * Foo foo = weld.instance().select(Foo.class).get();
     * </code>
     */
    Instance<Object> instance();

}
