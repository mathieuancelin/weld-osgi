package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
        List<String> filters = getFilter(qualifiers);
        String result = "";
        if(filters.size() > 1) {
            result = "(&";
            for(String filter : filters) {
                result += "(" + filter + ")";
            }
            result += ")";
        } else if(filters.size() == 1) {
            result = filters.get(0);
        }
        return new OSGiFilterQualifierType(result);
    }

    public static Filter makeFilter(Filter old, Annotation... qualifiers) {
        List<String> filters = getFilter(qualifiers);
        String result = "";
        if(old.value() != null && !old.value().equals("")) {
            if(filters.size() >= 1) {
                result = "(&";
                result += "(" + old.value() + ")";
                for(String filter : filters) {
                    result += "(" + filter + ")";
                }
                result += ")";
            } else {
                result = old.value();
            }
        } else if(filters.size() > 1) {
            result = "(&";
            for(String filter : filters) {
                result += "(" + filter + ")";
            }
            result += ")";
        } else if(filters.size() == 1) {
            result = filters.get(0);
        }
        return new OSGiFilterQualifierType(result);
    }

    private static List<String> getFilter(Annotation... qualifiers) {
        List<String> result = new ArrayList<String>();
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
                            filter = qualifier.annotationType().getSimpleName() + "." + m.getName() + "=" + value;
                            result.add(filter);
                        } catch (Throwable t) {
                            // ignore
                        }
                    }
                }
            }
        }
        return result;
    }
}
