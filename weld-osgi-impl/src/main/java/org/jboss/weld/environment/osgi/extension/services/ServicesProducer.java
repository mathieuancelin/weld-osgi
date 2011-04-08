package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.ParameterizedType;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Producers for Specific injected types;
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServicesProducer {

    @Produces
    public Bundle getBundle(BundleHolder holder, InjectionPoint p) {
        return holder.getBundle();
    }

    @Produces
    public BundleContext getBundleContext(BundleHolder holder, InjectionPoint p) {
        return holder.getContext();
    }

    @Produces
    public <T> ServicesImpl<T> getOSGiServices(BundleHolder holder, InjectionPoint p) {
        return new ServicesImpl<T>(((ParameterizedType)p.getType()).getActualTypeArguments()[0],
                p.getMember().getDeclaringClass(), holder.getContext());
    }

    @Produces
    public <T> ServiceImpl<T> getOSGiService(InjectionPoint p) {
        return new ServiceImpl<T>(((ParameterizedType)p.getType()).getActualTypeArguments()[0],
                p.getMember().getDeclaringClass());
    }
}
