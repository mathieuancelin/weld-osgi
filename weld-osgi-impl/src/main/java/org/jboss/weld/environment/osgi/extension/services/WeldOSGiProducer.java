package org.jboss.weld.environment.osgi.extension.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.Set;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.jboss.weld.environment.osgi.api.extension.BundleHeaders;
import org.jboss.weld.environment.osgi.api.extension.OSGiBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

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

    @Produces @OSGiBundle
    public Bundle getSpecificBundle(BundleHolder holder, InjectionPoint p) {
        Set<Annotation> qualifiers = p.getQualifiers();
        OSGiBundle bundle = null;
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(OSGiBundle.class)) {
                bundle = (OSGiBundle) qualifier;
                break;
            }
        }
        if (bundle.symbolicName().equals("")) {
            return getBundle(holder, p);
        }
        return (Bundle) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[] {Bundle.class},
                new BundleHandler(bundle.symbolicName(), bundle.version(), holder.getContext()));
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

    private static class BundleHandler implements InvocationHandler {

        private final String symbolicName;

        private Version version;

        private final BundleContext context;

        public BundleHandler(String symbolicName, String version, BundleContext context) {
            this.symbolicName = symbolicName;
            this.context = context;
            if (!version.equals("")) {
                this.version = new Version(version);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Bundle bundle = null;
            Bundle[] bundles = context.getBundles();
            if (bundles != null) {
                for (Bundle b : bundles) {
                    if (bundle != null) {
                        if (b.getSymbolicName().equals(symbolicName)) {
                            if (version != null) {
                                if (version.equals(b.getVersion())) {
                                    bundle = b;
                                    break;
                                }
                            } else {
                                bundle = b;
                                break;
                            }
                        }
                    }
                }
            }
            if (bundle == null) {
                System.out.println("Bundle " + symbolicName + " is unavailable.");
                return null;
            }
            return method.invoke(bundle, args);
        }
    }
}
