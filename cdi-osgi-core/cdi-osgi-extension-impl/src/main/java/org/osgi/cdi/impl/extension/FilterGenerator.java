package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.Property;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class FilterGenerator {

    public static Filter makeFilter() {
        return new OSGiFilterQualifierType("");
    }

    public static Filter make(Set<String> tokens) {
        if(tokens == null || tokens.size() == 0) {
            return new OSGiFilterQualifierType("");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (tokens.size() > 2){
            stringBuilder.append("(&");
        }
        for (String token : tokens) {
            stringBuilder.append(token);
        }
        if (tokens.size() > 2){
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

    public static Filter makeFilter(Publish publish) {
        return make(tokenize(publish));
    }

    public static Filter makeFilter(Publish publish, Collection<Annotation> annotations) {
        Set<String> tokens = new HashSet<String>();
        tokens.addAll(tokenize(publish));
        tokens.addAll(tokenize(annotations));
        return make(tokens);
    }
    
    public static Filter makeFilter(Filter old, String filter) {
        Set<String> tokens = new HashSet<String>();
        if(old != null && old.value() != null && old.value().length() > 0) {
            tokens.add(old.value());
        }
        tokens.add(filter);
        return make(tokens);
    }

    public static Filter makeFilter(Filter old, Collection<Annotation> annotations) {
        Set<String> tokens = new HashSet<String>();
        if(old != null && old.value() != null && old.value().length() > 0) {
            tokens.add(old.value());
        }
        tokens.addAll(tokenize(annotations));
        return make(tokens);
    }

    private static Set<String> tokenize(Publish publish) {
        Set<String> result = new HashSet<String>();
        for(Property property : publish.properties()) {
            result.add("(" + property.name() + "=" + property.value() + ")");
        }
        return result;
    }

    private static Set<String> tokenize(Collection<Annotation> annotations) {
        Set<String> result = new HashSet<String>();
        String current = "";
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                if (!annotation.annotationType().equals(Filter.class)) {
                    for (Method m : annotation.annotationType().getDeclaredMethods()) {
                        if (!m.isAnnotationPresent(Nonbinding.class)) {
                            try {
                                Object value = m.invoke(annotation);
                                if (value == null) {
                                    value = m.getDefaultValue();
                                }
                                current = "(" + annotation.annotationType().getSimpleName() + "." + m.getName() + "=" + value + ")";
                                result.add(current);
                            } catch (Throwable t) {// inaccessible property, skip
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
}
