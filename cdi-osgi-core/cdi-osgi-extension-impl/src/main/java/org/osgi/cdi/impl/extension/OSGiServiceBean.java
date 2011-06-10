package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.impl.extension.services.DynamicServiceHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
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
    private  Set<Annotation> qualifiers;
    private Filter filter;
    private boolean required = false;

    protected OSGiServiceBean(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
        type = injectionPoint.getType();
        required = false;
        qualifiers = injectionPoint.getQualifiers();
        for(Annotation qualifier: qualifiers) {
            if(qualifier.annotationType().equals(Filter.class)) {
                filter = (Filter)qualifier;
                break;
            }
        }
        filter = FilterGenerator.makeFilter(filter, qualifiers.toArray(new Annotation[qualifiers.size()]));

        System.out.println("## Registration of a new OSGiServiceBean: " + toString());
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
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Class getBeanClass() {
        return (Class)type;
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
    public Object create(CreationalContext ctx) {
        System.out.println("## Creation of a new OSGiServiceBean: " + toString());
        try {
            Bundle bundle = FrameworkUtil.getBundle(injectionPoint.getMember().getDeclaringClass());
            return Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class[]{getBeanClass()},
                    new DynamicServiceHandler(bundle, ((Class)type).getName(), filter));
        } catch (Exception e) {
            throw new CreationException(e);
        }
    }

    @Override
    public void destroy(Object instance, CreationalContext creationalContext) {
        // Nothing to do, services are unget after each call.
    }

    @Override
    public String toString() {
        return "OSGiServiceBean{" +
                "type=" + ((Class)type).getSimpleName() +
                ", filter=" + filter.value() +
                ", required=" + required +
                ", qualifiers=" + printQualifiers() +
                "}";
    }

    public String printQualifiers() {
        String result = "|";
        for(Annotation qualifier : getQualifiers()) {
            result += "@" + qualifier.annotationType().getSimpleName() + "|";
        }
        return result;
    }
}
