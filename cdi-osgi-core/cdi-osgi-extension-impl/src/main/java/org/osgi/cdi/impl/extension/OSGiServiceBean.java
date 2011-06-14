/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.impl.extension.services.DynamicServiceHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
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

        System.out.println("Registration of a new OSGiServiceBean: " + toString());
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
        Set<Annotation> result = new HashSet<Annotation>();
        result.addAll(qualifiers);
        result.add(new AnnotationLiteral<Any>() {});
        return result;
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
        System.out.println("Creation of a new OSGiServiceBean: " + toString());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OSGiServiceBean)) return false;

        OSGiServiceBean that = (OSGiServiceBean) o;

        if (required != that.required) return false;
        if (!filter.equals(that.filter)) return false;
        if (!injectionPoint.equals(that.injectionPoint)) return false;
        if (!qualifiers.equals(that.qualifiers)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + injectionPoint.hashCode();
        result = 31 * result + qualifiers.hashCode();
        result = 31 * result + filter.hashCode();
        result = 31 * result + (required ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OSGiServiceBean [" +
                ((Class)type).getSimpleName() +
                "] with qualifiers [" +
                printQualifiers() +
                "]";
    }

    public String printQualifiers() {
        String result = "";
        for(Annotation qualifier : getQualifiers()) {
            if(!result.equals("")) {
                result += " ";
            }
            result += "@" + qualifier.annotationType().getSimpleName();
            result += printValues(qualifier);
        }
        return result;
    }

    private String printValues(Annotation qualifier) {
        String result = "(";
        for (Method m : qualifier.annotationType().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(Nonbinding.class)) {
                try {
                    Object value = m.invoke(qualifier);
                    if (value == null) {
                        value = m.getDefaultValue();
                    }
                    if(value != null) {
                        result += m.getName() + "=" + value.toString();
                    }
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
        result += ")";
        return result.equals("()") ? "" : result;
    }
}
