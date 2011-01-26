package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.ParameterizedType;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Producers for Specific injected types;
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServicesProducer {

    @Produces
    public Bundle getBundle(InjectionPoint p) {
        Bundle bundle = FrameworkUtil.getBundle(p.getMember().getDeclaringClass());
        if (bundle != null)
            return bundle;
        else
            throw new IllegalStateException("Can't find bundle.");
    }

    @Produces
    public BundleContext getBundleContext(InjectionPoint p) {
        Bundle bundle = FrameworkUtil.getBundle(p.getMember().getDeclaringClass());
        if (bundle != null)
            return bundle.getBundleContext();
        else
            throw new IllegalStateException("Can't find bundle.");
    }

    @Produces
    public <T> ServicesImpl<T> getOSGiServices(InjectionPoint p) {
        return new ServicesImpl<T>(((ParameterizedType)p.getType()).getActualTypeArguments()[0],
                p.getMember().getDeclaringClass());
    }

    @Produces
    public <T> ServiceImpl<T> getOSGiService(InjectionPoint p) {
        return new ServiceImpl<T>(((ParameterizedType)p.getType()).getActualTypeArguments()[0],
                p.getMember().getDeclaringClass());
    }
}
