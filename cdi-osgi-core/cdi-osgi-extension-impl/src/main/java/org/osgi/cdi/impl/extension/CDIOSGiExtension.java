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

import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Required;
import org.osgi.cdi.impl.extension.services.*;
import org.osgi.cdi.impl.integration.InstanceHolder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Weld OSGi extension.
 *
 * Contains copy/paste parts from the GlasFish OSGI-CDI extension.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
@ApplicationScoped
public class CDIOSGiExtension implements Extension {

    // hack for weld integration
    public static ThreadLocal<Long> currentBundle = new ThreadLocal<Long>();

    private HashMap<Type, Set<InjectionPoint>> servicesToBeInjected = new HashMap<Type, Set<InjectionPoint>>();

    private HashMap<Type, Set<InjectionPoint>> serviceProducerToBeInjected = new HashMap<Type, Set<InjectionPoint>>();

    private List<Annotation> observers = new ArrayList<Annotation>();

    private Set<Class<?>> requiredOsgiServiceDependencies = new HashSet<Class<?>>();

    public void registerCDIOSGiBeans(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        event.addAnnotatedType(manager.createAnnotatedType(CDIOSGiProducer.class));
        event.addAnnotatedType(manager.createAnnotatedType(BundleHolder.class));
        event.addAnnotatedType(manager.createAnnotatedType(RegistrationsHolderImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ContainerObserver.class));
        event.addAnnotatedType(manager.createAnnotatedType(InstanceHolder.class));
    }

    public void discoverCDIOSGiClass(@Observes ProcessAnnotatedType<?> event) {
        AnnotatedType annotatedType = event.getAnnotatedType();
        annotatedType = discoverAndProcessCDIOSGiClass(annotatedType);
        event.setAnnotatedType(annotatedType);
    }

    public void discoverCDIOSGiServices(@Observes ProcessInjectionTarget<?> event) {
        Set<InjectionPoint> injectionPoints = event.getInjectionTarget().getInjectionPoints();
        discoverServiceInjectionPoints(injectionPoints);
    }

    public void afterProcessProducer(@Observes ProcessProducer<?,?> event) {
        //Only using ProcessInjectionTarget for now.
        //TODO do we need to scan these events
    }

    public void afterProcessBean(@Observes ProcessBean<?> event){
        //ProcessInjectionTarget and ProcessProducer take care of all relevant injection points.
        //TODO verify that :)
    }

    public void registerObservers(@Observes ProcessObserverMethod<?,?> event) {
        Set<Annotation> qualifiers = event.getObserverMethod().getObservedQualifiers();
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Filter.class)) {
                observers.add(qualifier);
            }
        }
    }
    
    public void registerCDIOSGiServices(@Observes AfterBeanDiscovery event) {
        for (Iterator<Type> iterator = this.servicesToBeInjected.keySet().iterator();iterator.hasNext();) {
            Type type =  iterator.next();
            if (!(type instanceof Class)) {
                //TODO: need to handle Instance<Class>. This fails currently
                System.out.println("Unknown type:" + type);
                event.addDefinitionError(
                    new UnsupportedOperationException("Injection target type " + type + "not supported"));
                break;
            }
            addService(event, this.servicesToBeInjected.get(type));
        }

        for (Iterator<Type> iterator = this.serviceProducerToBeInjected.keySet().iterator(); iterator.hasNext();) {
            Type type =  iterator.next();
            addServiceProducer(event, this.serviceProducerToBeInjected.get(type));
        }
    }

    private AnnotatedType discoverAndProcessCDIOSGiClass(AnnotatedType annotatedType) {
        return new CDIOSGiAnnotatedType(annotatedType);
    }

    private void discoverServiceInjectionPoints(Set<InjectionPoint> injectionPoints) {
        for (Iterator<InjectionPoint> iterator = injectionPoints.iterator(); iterator.hasNext();) {
            InjectionPoint injectionPoint = iterator.next();

            boolean service = false;
            try {
                if (((ParameterizedType) injectionPoint.getType()).getRawType().equals(Service.class)) {
                    service = true;
                }
            } catch (Exception e) {//Not a ParameterizedType, skip
            }

            if (service) {
                addServiceProducerInjectionInfo(injectionPoint);
            } else if (contains(injectionPoint.getQualifiers(), OSGiService.class)) {
                if (contains(injectionPoint.getQualifiers(), Required.class)) {
                    requiredOsgiServiceDependencies.add((Class) injectionPoint.getType());
                }
                addServiceInjectionInfo(injectionPoint);
            }
        }
    }

    private void addServiceInjectionInfo(InjectionPoint injectionPoint) {
        Type key = injectionPoint.getType();
        if (!servicesToBeInjected.containsKey(key)){
            servicesToBeInjected.put(key, new HashSet<InjectionPoint>());
        }
        servicesToBeInjected.get(key).add(injectionPoint);
    }

    private void addServiceProducerInjectionInfo(InjectionPoint injectionPoint) {
        Type key = injectionPoint.getType();
        if (!serviceProducerToBeInjected.containsKey(key)){
            serviceProducerToBeInjected.put(key, new HashSet<InjectionPoint>());
        }
        serviceProducerToBeInjected.get(key).add(injectionPoint);
    }
    
    private void addService(AfterBeanDiscovery event, final Set<InjectionPoint> injectionPoints) {
        Set<OSGiServiceBean> beans = new HashSet<OSGiServiceBean>();
        for (Iterator<InjectionPoint> iterator = injectionPoints.iterator(); iterator.hasNext();) {
            final InjectionPoint injectionPoint = iterator.next();
            beans.add(new OSGiServiceBean(injectionPoint));
        }
        for(OSGiServiceBean bean : beans) {
            event.addBean(bean);
        }
    }

    private void addServiceProducer(AfterBeanDiscovery event, final Set<InjectionPoint> injectionPoints) {
        Set<OSGiServiceProducerBean> beans = new HashSet<OSGiServiceProducerBean>();
        for (Iterator<InjectionPoint> iterator = injectionPoints.iterator(); iterator.hasNext();) {
            final InjectionPoint injectionPoint = iterator.next();
            beans.add(new OSGiServiceProducerBean(injectionPoint));
        }
        for(OSGiServiceProducerBean bean : beans) {
            event.addBean(bean);
        }
    }

    private boolean contains(Set<Annotation> qualifiers, Class<? extends Annotation> qualifier) {
        for (Iterator<Annotation> iterator = qualifiers.iterator();iterator.hasNext();) {
            if(iterator.next().annotationType().equals(qualifier)) {
                return true;
            }
        }
        return false;
    }

    public List<Annotation> getObservers() {
        return observers;
    }

    public Set<Class<?>> getRequiredOsgiServiceDependencies() {
        return requiredOsgiServiceDependencies;
    }
}
