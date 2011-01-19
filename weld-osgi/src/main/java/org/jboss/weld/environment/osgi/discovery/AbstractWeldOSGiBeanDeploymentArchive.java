package org.jboss.weld.environment.osgi.discovery;

import java.util.Collection;
import java.util.Collections;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.ejb.spi.EjbDescriptor;

/**
 * Implements the basic requirements of a {@link BeanDeploymentArchive} (bean
 * archive id and service registry).
 * 
 * Suitable for extension by those who need to build custom
 * {@link BeanDeploymentArchive} implementations.
 * 
 * @see MutableBeanDeploymentArchive
 * @see ImmutableBeanDeploymentArchive
 * 
 * @author Pete Muir
 * 
 */
public abstract class AbstractWeldOSGiBeanDeploymentArchive implements BeanDeploymentArchive {

    private final ServiceRegistry serviceRegistry;
    private final String id;

    public AbstractWeldOSGiBeanDeploymentArchive(String id) {
        this.id = id;
        this.serviceRegistry = new SimpleServiceRegistry();
    }

    @Override
    public Collection<EjbDescriptor<?>> getEjbs() {
        return Collections.emptyList();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ServiceRegistry getServices() {
        return serviceRegistry;
    }
}
