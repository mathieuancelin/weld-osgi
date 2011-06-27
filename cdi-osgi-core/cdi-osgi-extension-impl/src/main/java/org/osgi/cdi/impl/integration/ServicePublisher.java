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
import org.osgi.cdi.impl.SoutLogger;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.services.RegistrationsHolderImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This is a class scanner that auto-publishes OSGi services from @Publish annotated classes.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class ServicePublisher {

//    private Logger logger = LoggerFactory.getLogger(getClass());
    private SoutLogger logger = new SoutLogger();

    private final Collection<String> classes;
    private final Bundle bundle;
    private final Instance<Object> instance;
    private final Set<String> blackList;

    public ServicePublisher(Collection<String> classes, Bundle bundle, Instance<Object> instance, Set<String> blackList) {
        logger.debug("Contructing a new ServicePublisher for {}",bundle);
        this.classes = classes;
        this.bundle = bundle;
        this.instance = instance;
        this.blackList = blackList;
    }

    public void registerAndLaunchComponents() {
        Class<?> clazz;
        for (String className : classes) {
            logger.trace("Scanning class {}",className);
            try {
                clazz = bundle.loadClass(className);
            } catch (Exception e) {// inaccessible class
                logger.warn("Inaccessible class {}, skipping for next class",className);
                continue;
            }
            //is an auto-publishable class?
            if (clazz.isAnnotationPresent(Publish.class)) {
                logger.debug("Found a auto-publishable class {}", className);
                Object service = null;
                InstanceHolder instanceHolder = instance.select(InstanceHolder.class).get();
                List<Annotation> qualifiers = getQualifiers(clazz);
                try {
                    Instance instance = instanceHolder.select(clazz, qualifiers.toArray(new Annotation[qualifiers.size()]));
                    service = instance.get();
                } catch (Throwable e) {
                    logger.error("###Unable to instantiate the service {} with qualifiers {}",className,qualifiers);
                    throw new RuntimeException(e);
                }
                publish(clazz, service, qualifiers);
            }
        }
    }

    private void publish(Class<?> clazz, Object service, List<Annotation> qualifiers) {
        ServiceRegistration registration = null;
        Publish publish = clazz.getAnnotation(Publish.class);
        Class[] contracts = publish.contracts();
        Properties properties = getServiceProperties(publish, qualifiers);
        if(contracts.length > 0) {// if there are contracts
            logger.debug("Registering class {} with contracts",clazz.getName());
            logger.trace("Contracts are: {}",contracts);
            String[] names = new String[contracts.length];
            for(int i = 0;i < contracts.length;i++) {
                names[i] = contracts[i].getName();
            }
            registration = bundle.getBundleContext().registerService(names, service, properties);
        } else {
            if(clazz.getInterfaces().length > 0) {
                List<Class> interfaces = new ArrayList<Class>();
                for (Class itf : clazz.getInterfaces()) {
                    if (!blackList.contains(itf.getName())) {
                        interfaces.add(itf);
                    }
                }
                contracts = interfaces.toArray(new Class[interfaces.size()]);
            }
            if(contracts.length > 0) {// if there are non-blacklisted interfaces
                logger.debug("Registering class {} with interfaces",clazz.getName());
                logger.trace("Interfaces are: {}",contracts);
                String[] names = new String[contracts.length];
                for(int i = 0;i < contracts.length;i++) {
                    names[i] = contracts[i].getName();
                }
                registration = bundle.getBundleContext().registerService(names, service, properties);
            } else {
                Class superClass = clazz.getClass().getSuperclass();
                if(superClass != null && superClass != Object.class && !blackList.contains(superClass.getName())) {// if there is a non blacklisted superclass
                    logger.debug("Registering class {} with its superclass",clazz.getName());
                    logger.trace("Superclass is: {}",superClass.getName());
                    registration = bundle.getBundleContext().registerService(superClass.getName(), service, properties);
                } else {// publish directly with the implementation type
                    logger.debug("Registering class {} with its own type",clazz.getName());
                    registration = bundle.getBundleContext().registerService(clazz.getName(), service, properties);
                }
            }
        }
        if (registration != null) {
            CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
            instance.select(RegistrationsHolderImpl.class).get().addRegistration(registration);
        } else {
            logger.warn("The registration of {} did not occurred",clazz.getName());
        }
    }

    private static Properties getServiceProperties(Publish publish, List<Annotation> qualifiers) {
        Properties properties = new Properties();
        if (!qualifiers.isEmpty()) {
            for (Annotation qualifier : qualifiers) {
                for (Method m : qualifier.annotationType().getDeclaredMethods()) {
                    if (!m.isAnnotationPresent(Nonbinding.class)) {
                        try {
                            String key = qualifier.annotationType().getSimpleName() + "." + m.getName();
                            Object value = m.invoke(qualifier);
                            if (value == null) {
                                value = m.getDefaultValue();
                            }
                            properties.setProperty(key, value.toString());
                        } catch (Throwable t) {// ignore
                        }
                    }
                }
            }
        }
        if (publish.properties().length > 0) {
            for (Property property : publish.properties()) {
                properties.setProperty(property.name(), property.value());
            }
        }
        return properties;
    }

    private static List<Annotation> getQualifiers(Class<?> clazz) {
        List<Annotation> qualifiers = new ArrayList<Annotation>();
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                qualifiers.add(annotation);
            }
        }
        return qualifiers;
    }

}
