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

import org.osgi.cdi.api.extension.annotation.*;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for generating {@link Filter} qualifier from various sources (other {@link Filter}, {@link Qualifier}, {@link String} ...).
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class FilterGenerator {

    public static Filter makeFilter() {
        return new OSGiFilterQualifierType("");
    }

    public static Filter make(Set<String> tokens) {
        if(tokens == null || tokens.size() == 0) {
            return new OSGiFilterQualifierType("");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (tokens.size() > 1){
            stringBuilder.append("(&");
        }
        for (String token : tokens) {
            stringBuilder.append(token);
        }
        if (tokens.size() > 1){
            stringBuilder.append(")");
        }
        return new OSGiFilterQualifierType(stringBuilder.toString());
    }

    public static Filter makeFilter(String filter) {
        return new OSGiFilterQualifierType(filter);
    }

    public static Filter makeFilter(List<Annotation> annotations) {
        return make(tokenize(annotations));
    }

    public static Filter normalize(Filter filter) {
        return make(tokenize(filter));
    }

    public static Filter makeFilter(Publish publish) {
        return make(tokenize(publish));
    }

    public static Filter makeFilter(InjectionPoint injectionPoint) {
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        Filter filter = null;
        for(Annotation qualifier : qualifiers) {
            if(qualifier.annotationType().equals(Filter.class)) {
                filter = (Filter)qualifier;
                break;
            }
        }
        return FilterGenerator.makeFilter(filter, qualifiers);
    }

    public static Filter makeFilter(Publish publish, Collection<Annotation> annotations) {
        Set<String> tokens = new HashSet<String>();
        tokens.addAll(tokenize(publish));
        tokens.addAll(tokenize(annotations));
        return make(tokens);
    }
    
    public static Filter makeFilter(Filter old, String filter) {
        Set<String> tokens = new HashSet<String>();
        tokens.addAll(tokenize(old));
        tokens.add(filter);
        return make(tokens);
    }

    public static Filter makeFilter(Filter old, Collection<Annotation> annotations) {
        Set<String> tokens = new HashSet<String>();
        tokens.addAll(tokenize(old));
        tokens.addAll(tokenize(annotations));
        return make(tokens);
    }

    private static Set<String> tokenize(Publish publish) {
        Set<String> result = new HashSet<String>();
        for(Property property : publish.properties()) {
            result.add("(" + property.name().toLowerCase() + "=" + property.value() + ")");
        }
        return result;
    }

    private static Set<String> tokenize(Filter filter) {
        Set<String> result = new HashSet<String>();
        if(filter != null && filter.value() != null && filter.value().length() > 0) {
            result.add(filter.value());
        }
        return result;
    }

    private static Set<String> tokenize(Collection<Annotation> annotations) {
        Set<String> result = new HashSet<String>();
        String current = "";
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                if (annotation.annotationType().equals(Filter.class)) {
                    result.addAll(tokenize((Filter)annotation));
                } else if(!annotation.annotationType().equals(Required.class)
                        && !annotation.annotationType().equals(OSGiService.class)
                        && !annotation.annotationType().equals(Default.class)
                        && !annotation.annotationType().equals(Any.class)) {
                    if (annotation.annotationType().getDeclaredMethods().length > 0) {
                        for (Method m : annotation.annotationType().getDeclaredMethods()) {
                            if (!m.isAnnotationPresent(Nonbinding.class)) {
                                try {
                                    Object value = m.invoke(annotation);
                                    if (value == null) {
                                        value = m.getDefaultValue();
                                        if(value == null) {
                                            value = "*";
                                        }
                                    }
                                    current = "(" + annotation.annotationType().getSimpleName().toLowerCase()
                                              + "." + m.getName().toLowerCase()
                                              + "=" + value + ")";
                                    result.add(current);
                                } catch (Throwable t) {// inaccessible property, skip
                                }
                            }
                        }
                    } else {
                        current = "(" + annotation.annotationType().getSimpleName().toLowerCase() + "=*)";
                        result.add(current);
                    }
                }
            }
        }
        return result;
    }
}
