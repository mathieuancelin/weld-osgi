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

package org.osgi.cdi.impl.integration;

import org.osgi.cdi.api.extension.annotation.Property;
import org.osgi.cdi.api.extension.annotation.Publish;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.services.RegistrationsHolderImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class ServicePublisher {

    private final Collection<String> classes;
    private final Bundle bundle;
    private final Instance<Object> instance;
    private final Set<String> blackList;

    public ServicePublisher(Collection<String> classes, Bundle bundle,
                            Instance<Object> instance, Set<String> blackList) {
        this.classes = classes;
        this.bundle = bundle;
        this.instance = instance;
        this.blackList = blackList;
    }

    public void registerAndLaunchComponents() {
        System.out.println(String.format("\nRegistering/Starting OSGi Service for bundle %s\n", bundle.getSymbolicName()));
        for (String className : classes) {
            Class<?> clazz = null;
            try {
                clazz = bundle.loadClass(className);
            } catch (Exception e) {//inaccessible class, silently ignore :-)
            }
            if (clazz != null) {
                //is an auto-publishable class?
                if (clazz.isAnnotationPresent(Publish.class)) {
                    Object service = null;
                    InstanceHolder instanceHolder = instance.select(InstanceHolder.class).get();
                    List<Annotation> qualifiers = getQualifiers(clazz);
                    try {
                        Instance instance = instanceHolder.select(clazz, qualifiers.toArray(new Annotation[qualifiers.size()]));
                        service = instance.get();
                    } catch (Throwable e) {
                        throw new RuntimeException("Unable to instantiate the service, CDI return this error: " + e.getMessage());
                    }
                    publish(clazz, service, qualifiers);
                }
            }
        }
    }

    private void publish(Class<?> clazz, Object service, List<Annotation> qualifiers) {
        // register service
        Annotation[] annotations = qualifiers.toArray(new Annotation[qualifiers.size()]);
        ServiceRegistration registration = null;
        if (service != null) {
            Publish publish = clazz.getAnnotation(Publish.class);
            Class[] contracts = publish.contracts();
            Properties properties = getServiceProperties(publish, qualifiers);
            if (contracts.length != 0) {//if contracts are precised
                for (Class contract : contracts) {
                    System.out.println("Registering OSGi service " + clazz.getName() + " as " + contract.getName());
                    registration = bundle.getBundleContext().registerService(
                            contract.getName(), getProxy(contract, clazz, annotations, bundle), properties);
                }
            } else { //else use service interfaces or type
                if (service.getClass().getInterfaces().length > 0) {
                    for (Class interfaces : service.getClass().getInterfaces()) {
                        if (!blackList.contains(interfaces.getName())) {
                            System.out.println("Registering OSGi service " + clazz.getName() + " as " + interfaces.getName());
                            registration = bundle.getBundleContext().registerService(
                                    interfaces.getName(), getProxy(interfaces, clazz, annotations, bundle), properties);
                        }
                    }
                }
            }
            if(registration == null) {
                System.out.println("Registering OSGi service " + clazz.getName() + " as " + clazz.getName());
                registration = bundle.getBundleContext().registerService(clazz.getName(), service, properties);
            }
        }
        if (registration != null) {
            CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
            instance.select(RegistrationsHolderImpl.class).get().addRegistration(registration);
        }
    }

    private static Properties getServiceProperties(Publish publish, List<Annotation> qualifiers) {
        Properties properties = null;
        if (!qualifiers.isEmpty()) {
            properties = new Properties();
            for (Annotation qualif : qualifiers) {
                for (Method m : qualif.annotationType().getDeclaredMethods()) {
                    if (!m.isAnnotationPresent(Nonbinding.class)) {
                        try {
                            String key = qualif.annotationType().getSimpleName() + "." + m.getName();
                            Object value = m.invoke(qualif);
                            if (value == null) {
                                value = m.getDefaultValue();
                            }
                            properties.setProperty(key, value.toString());
                        } catch (Throwable t) {
                            // ignore
                        }
                    }
                }
            }
        }
        if (publish.properties().length > 0) {
            properties = new Properties();
            for (Property property : publish.properties()) {
                properties.setProperty(property.name(), property.value());
            }
        }
        return properties;
    }

    private static List<Annotation> getQualifiers(Class<?> clazz) {
        List<Annotation> qualifiers = new ArrayList<Annotation>();
        for (Annotation a : clazz.getAnnotations()) {
            if (a.annotationType().isAnnotationPresent(Qualifier.class)) {
                qualifiers.add(a);
            }
        }
        return qualifiers;
    }

    private <T> T getProxy(Class<T> interf, Class<? extends T> clazz, Annotation[] qualifiers, Bundle bundle) {
        return interf.cast(
                Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[]{interf},
                    new LazyService(clazz, qualifiers, bundle)));
    }

    private class LazyService implements InvocationHandler {

        private final Class<?> contract;
        private final Annotation[] qualifiers;
        private final Bundle bundle;

        public LazyService(Class<?> contract, Annotation[] qualifiers, Bundle bundle) {
            this.contract = contract;
            this.qualifiers = qualifiers;
            this.bundle = bundle;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
                InstanceHolder instanceHolder = instance.select(InstanceHolder.class).get();
                return method.invoke(
                        instanceHolder.select(contract, qualifiers).get(),
                        args);
            } finally {
                CDIOSGiExtension.currentBundle.remove();
            }
        }
    }
}
