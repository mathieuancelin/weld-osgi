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
import org.osgi.cdi.impl.extension.services.ServiceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class OSGiServiceProducerBean<Service> implements Bean<Service> {

    private final Type type;
    private final InjectionPoint injectionPoint;
    private Filter filter;

    protected OSGiServiceProducerBean(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
        type = injectionPoint.getType();
        filter = new OSGiFilterQualifierType("");

        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Filter.class)) {
                filter = (Filter) qualifier;
                break;
            }
        }
        System.out.println("New registered service producer bean: " + toString());
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
        s.add(new AnnotationLiteral<Any>() {
        });
        s.add(filter);
        return s;
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
        Set<InjectionPoint> injectionPoints = new HashSet<InjectionPoint>();
        injectionPoints.add(injectionPoint);
        return injectionPoints;
    }

    @Override
    public Service create(CreationalContext creationalContext) {
        System.out.println("Creation of a new OSGiServiceProducerBean: " + toString());
        BundleContext registry = FrameworkUtil.getBundle(injectionPoint.getMember().getDeclaringClass()).getBundleContext();
        return (Service) new ServiceImpl(((ParameterizedType) type).getActualTypeArguments()[0], registry, filter);
    }

    @Override
    public void destroy(Service instance, CreationalContext<Service> creationalContext) {
        // Nothing to do, services are unget after each call.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OSGiServiceProducerBean)) return false;

        OSGiServiceProducerBean that = (OSGiServiceProducerBean) o;

        if (!filter.equals(that.filter)) return false;
        if (!injectionPoint.equals(that.injectionPoint)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + injectionPoint.hashCode();
        result = 31 * result + filter.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OSGiServiceProducerBean{" +
                "type=" + "Service<" + ((Class)((ParameterizedType) type).getActualTypeArguments()[0]).getSimpleName() + ">" +
                ", filter=" + filter.value() +
                ", qualifiers=" + printQualifiers() +
                '}';
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
