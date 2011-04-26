package org.jboss.weld.environment.osgi.extension;

import org.jboss.weld.environment.osgi.extension.services.ServiceImpl;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.framework.FrameworkUtil;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class FilteredServiceBean<Service> implements Bean<Service> {

    private final Type type;
    private final InjectionPoint injectionPoint;
    private Filter filter;

    protected FilteredServiceBean(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
        this.type = this.injectionPoint.getType();
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Filter.class)) {
                filter = (Filter) qualifier;
                break;
            }
        }
    }

    @Override
    public Set<Type> getTypes() {
        Set<Type> s = new HashSet<Type>();
        s.add(type);
        s.add(Object.class);
        return s;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> s = new HashSet<Annotation>();
        s.add(new AnnotationLiteral<Default>() {
        });
        s.add(new AnnotationLiteral<Any>() {
        });
        s.add(new OSGiFilterQualifierType(filter.value()));
        return s;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public String getName() {
        return type.toString();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Class getBeanClass() {
        return ((Class) ((ParameterizedType) type).getRawType());
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public Service create(CreationalContext creationalContext) {
        return (Service) new ServiceImpl(((ParameterizedType) type).getActualTypeArguments()[0], FrameworkUtil.getBundle(
                injectionPoint.getMember().getDeclaringClass()).getBundleContext(), filter);
    }

    @Override
    public void destroy(Service instance, CreationalContext<Service> creationalContext) {
        // Nothing to do, services are unget after each call.
    }
}
