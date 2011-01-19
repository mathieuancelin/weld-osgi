package org.jboss.weld.environment.osgi.discovery;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;

/**
 * Implements the basic requirements of a {@link Deployment}. Provides a service
 * registry.
 * 
 * Suitable for extension by those who need to build custom {@link Deployment}
 * implementations.
 * 
 * @author Pete Muir
 * 
 */
public abstract class AbstractWeldOSGiDeployment implements Deployment {

    public static final String[] RESOURCES = {"META-INF/beans.xml"};
    private final ServiceRegistry serviceRegistry;
    private final Iterable<Metadata<Extension>> extensions;

    public AbstractWeldOSGiDeployment(Bootstrap bootstrap) {
        this.serviceRegistry = new SimpleServiceRegistry();
        this.extensions = bootstrap.loadExtensions(getClass().getClassLoader());
    }

    @Override
    public ServiceRegistry getServices() {
        return serviceRegistry;
    }

    @Override
    public Iterable<Metadata<Extension>> getExtensions() {
        return extensions;
    }
}
