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
        String f = "(&(" + old.value() + ")(" + filter + "))";
        return new OSGiFilterQualifierType(f);
    }

    public static Filter makeFilter(Annotation... qualifiers) {
        String f = getFilter(qualifiers);
        return new OSGiFilterQualifierType(f);
    }

    public static Filter makeFilter(Filter old, Annotation... qualifiers) {
        String f = "(&(" + old.value() + ")(" + getFilter(qualifiers) + "))";
        return new OSGiFilterQualifierType(f);
    }

    private static String getFilter(Annotation... qualifiers) {
        String filter = "(&";
        for(Annotation qualifier: qualifiers) {
            for (Method m : qualifier.annotationType().getDeclaredMethods()) {
                    if (!m.isAnnotationPresent(Nonbinding.class)) {
                        try {
                            filter += "(" + qualifier.annotationType().getName() + "." + m.getName();
                            Object value = m.invoke(qualifier);
                            if (value == null) {
                                value = m.getDefaultValue();
                            }
                            filter += "=" + value == null ? "" : value.toString() + ")";
                        } catch (Throwable t) {
                            // ignore
                        }
                    }
                }
        }
        filter += ")";
        return filter;
    }
}
