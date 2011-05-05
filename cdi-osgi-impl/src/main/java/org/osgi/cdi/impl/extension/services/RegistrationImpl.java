package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.Registration;
import org.osgi.cdi.api.extension.RegistrationHolder;
import org.osgi.cdi.api.extension.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.blueprint.reflect.MapEntry;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu Clochard
 */
public class RegistrationImpl<T> implements Registration<T> {

    private final Class<T> contract;
    private final BundleContext registry;
    private final Bundle bundle;
    private final RegistrationHolder holder;
    private List<Registration<T>> registrations = new ArrayList<Registration<T>>();

    public RegistrationImpl(Class<T> contract,
            BundleContext registry, Bundle bundle,
            RegistrationHolder holder) {
        this.contract = contract;
        this.registry = registry;
        this.holder = holder;
        this.bundle = bundle;
    }

    @Override
    public void unregister() {
        for(ServiceRegistration reg : holder.getRegistrations()) {
            holder.removeRegistration(reg);
            reg.unregister();
        }
    }

    @Override
    public <T> Service<T> getServiceReference() {
        return new ServiceImpl<T>(contract, registry);
    }

    @Override
    public Registration<T> select(Annotation... qualifiers) {
        if (qualifiers == null) {
            throw new IllegalArgumentException("You can't pass null array of qualifiers");
        }
        String filter = constructFilter(qualifiers);
        return null;
    }

    @Override
    public Registration<T> select(String filter) {
        Filter osgiFilter = null;
        try {
            osgiFilter = FrameworkUtil.createFilter(filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid LDAP filter : " + e.getMessage());
        }
        RegistrationHolder holder = new RegistrationsHolderImpl();
        for(ServiceRegistration registration : holder.getRegistrations()) {
            if(osgiFilter.match(registration.getReference())) {
                holder.addRegistration(registration);
            }
        }
        return new RegistrationImpl<T>(contract,registry,bundle,holder);
    }

    @Override
    public int size() {
        return holder.size();
    }

    @Override
    public Iterator<Registration<T>> iterator() {
        populate();
        return registrations.iterator();
    }

    private void populate() {
        registrations.clear();
        try {
            List<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                registrations.add(new RegistrationImpl<T>(contract, registry, bundle, holder));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String constructFilter(Annotation... qualifiers) {
        Map<String, String> properties = new HashMap<String, String>();
        String key = "";
        String value = "";
        for(Annotation qualifier : qualifiers) {
            Class<?> qualifierClass = qualifier.annotationType();
            if(qualifierClass.getAnnotation(Qualifier.class) == null) {
                throw new IllegalArgumentException("You should only provide @Qualifier annotation");
            }
            try {
                Method getValue = qualifierClass.getMethod("value", qualifierClass);
                value = (String) getValue.invoke(qualifier);
            } catch (NoSuchMethodException e) {
                value = "";
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Qualifier value inaccessible : " + e.getClass().getSimpleName() + " " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Qualifier value inaccessible : " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
            key = qualifier.annotationType().getSimpleName();
            properties.put(key,value);
        }
        String result = "(&";
        for (Map.Entry<String,String> propertie : properties.entrySet()) {
            result += "(";
            result += propertie.getKey();
            result += " = ";
            result += propertie.getValue();
            result += ")";
        }
        result += ")";
        return result;
    }

}
