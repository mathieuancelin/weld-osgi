package org.jboss.weld.environment.osgi.extension;

import org.jboss.weld.environment.osgi.extension.services.DynamicServiceHandler;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Required;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiServiceBean implements Bean {

    private final Type type;
    private final InjectionPoint injectionPoint;
    private Filter filter;
    private boolean required = false;

    protected OSGiServiceBean(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
        this.type = this.injectionPoint.getType();
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Filter.class)) {
                filter = (Filter) qualifier;
            }
            if (qualifier.annotationType().equals(Required.class)) {
                required = true;
            }
        }
    }

    @Override
    public Object create(CreationalContext ctx) {
        try {
            Type serviceType = injectionPoint.getType();
            Class serviceClass = ((Class) (serviceType));
            String serviceName = serviceClass.getName();
            Bundle bundle = FrameworkUtil.getBundle(
                    injectionPoint.getMember().getDeclaringClass());
            return Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class[]{(Class) serviceClass},
                    new DynamicServiceHandler(bundle, serviceName, filter));
        } catch (Exception e) {
            throw new CreationException(e);
        }
    }

    @Override
    public void destroy(Object instance,
            CreationalContext creationalContext) {
        // Nothing to do, services are unget after each call.
    }

    @Override
    public Class getBeanClass() {
        return (Class) type;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public String getName() {
        return type.toString();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> s = new HashSet<Annotation>();
        s.add(new AnnotationLiteral<Default>() {
        });
        s.add(new AnnotationLiteral<Any>() {
        });
        s.add(new AnnotationLiteral<OSGiService>() {
        });
        //s.add(new OSGiServiceQualifierType());
        if (filter != null) {
            s.add(new OSGiFilterQualifierType(filter.value()));
        }
        if (required) {
            s.add(new AnnotationLiteral<Required>() {
            });
        }
        return s;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Set<Type> getTypes() {
        Set<Type> s = new HashSet<Type>();
        s.add(type);
        s.add(Object.class);
        return s;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    private final class OSGiServiceQualifierType
        extends AnnotationLiteral<OSGiService>
            implements OSGiService {
    }
}
