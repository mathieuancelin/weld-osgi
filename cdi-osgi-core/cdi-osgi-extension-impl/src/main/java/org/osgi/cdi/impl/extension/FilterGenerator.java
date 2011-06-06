package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: matthieu
 * Date: 01/06/11
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class FilterGenerator {

    public static Filter makeFilter() {
        return new OSGiFilterQualifierType("");
    }

    public static Filter makeFilter(String filter) {
        return new OSGiFilterQualifierType(filter);
    }

    public static Filter makeFilter(Filter old, String filter) {
        String f = "";
        if(old.value() != null && !old.value().equals("")) {
            if(filter != null && !filter.equals("")) {
                f = "(&(" + old.value() + ")(" + filter + "))";
            } else {
                f = old.value();
            }
        } else if(filter != null) {
            f = filter;
        }
        return new OSGiFilterQualifierType(f);
    }

    public static Filter makeFilter(Annotation... qualifiers) {
        String f = getFilter(qualifiers);
        return new OSGiFilterQualifierType(f);
    }

    public static Filter makeFilter(Filter old, Annotation... qualifiers) {
        String f = "";
        String filter = getFilter(qualifiers);
        if(old.value() != null && !old.value().equals("")) {
            if(filter != null && !filter.equals("")) {
                f = "(&(" + old.value() + ")(" + filter + "))";
            } else {
                f = old.value();
            }
        } else if(filter != null) {
            f = filter;
        }
        return new OSGiFilterQualifierType(f);
    }

    private static String getFilter(Annotation... qualifiers) {
        String filter = "";
        for (Annotation qualifier : qualifiers) {
            if (!qualifier.annotationType().equals(Filter.class)) {
                for (Method m : qualifier.annotationType().getDeclaredMethods()) {
                    if (!m.isAnnotationPresent(Nonbinding.class)) {
                        try {
                            Object value = m.invoke(qualifier);
                            if (value == null) {
                                value = m.getDefaultValue();
                            }
                            filter += "(" + qualifier.annotationType().getSimpleName() + "." + m.getName() + "=" + value + ")";
                        } catch (Throwable t) {
                            // ignore
                        }
                    }
                }
            }
        }
        if (filter != null && !filter.equals("")) {
            filter = "(&" + filter + ")";
        }
        return filter;
    }
}
