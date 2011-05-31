package org.jboss.weld.environment.osgi.integration.discovery;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.environment.osgi.integration.OSGiProxyService;
import org.jboss.weld.serialization.spi.ProxyServices;

import javax.enterprise.inject.spi.Extension;

/**
 * Implements the basic requirements of a {@link Deployment}. Provides a service registry.
 * <p/>
 * Suitable for extension by those who need to build custom {@link Deployment} implementations.
 *
 * @author Pete Muir
 */
public abstract class AbstractWeldOSGiDeployment implements Deployment {

    private final ServiceRegistry serviceRegistry;
    private final Iterable<Metadata<Extension>> extensions;

    public AbstractWeldOSGiDeployment(Bootstrap bootstrap) {
        this.serviceRegistry = new SimpleServiceRegistry();
        this.serviceRegistry.add(ProxyServices.class, new OSGiProxyService());
        // OK, Here we can install our own Extensions instances
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
