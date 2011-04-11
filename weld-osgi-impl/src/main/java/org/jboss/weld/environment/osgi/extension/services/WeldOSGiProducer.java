package org.jboss.weld.environment.osgi.extension.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Dictionary;
import java.util.Set;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.jboss.weld.environment.osgi.api.extension.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Producers for Specific injected types;
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldOSGiProducer {

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

    @Produces
    public Dictionary getBundleHeaders(BundleHolder holder) {
        return holder.getBundle().getHeaders();
    }

    @Produces @BundleHeaders
    public String getSpecificBundleHeaders(BundleHolder holder, InjectionPoint p) {
        Set<Annotation> qualifiers = p.getQualifiers();
        BundleHeaders headers = null;
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(BundleHeaders.class)) {
                headers = (BundleHeaders) qualifier;
                break;
            }
        }
        if (headers == null) {
            throw new IllegalStateException("You must specify a key for your BundleHeaders qualifier");
        }
        if (headers.value().equals("")) {
            return null; // not cool at all ...
        }
        return (String) holder.getBundle().getHeaders().get(headers.value());
    }
}
