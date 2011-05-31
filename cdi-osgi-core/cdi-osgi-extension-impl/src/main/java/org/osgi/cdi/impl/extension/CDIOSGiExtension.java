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
 */
@ApplicationScoped
public class CDIOSGiExtension implements Extension {

    // hack for weld integration
    public static ThreadLocal<Long> currentBundle =
            new ThreadLocal<Long>();

    private HashMap<Type, Set<InjectionPoint>> servicesToBeInjected
                            = new HashMap<Type, Set<InjectionPoint>>();

    private HashMap<Type, Set<InjectionPoint>> filteredServiceToBeInjected
                            = new HashMap<Type, Set<InjectionPoint>>();

    private List<Annotation> observers = new ArrayList<Annotation>();

    private Set<Class<?>> requiredOsgiServiceDependencies = new HashSet<Class<?>>();

    public void registerWeldOSGiBeans(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        event.addAnnotatedType(manager.createAnnotatedType(CDIOSGiProducer.class));
        event.addAnnotatedType(manager.createAnnotatedType(BundleHolder.class));
        event.addAnnotatedType(manager.createAnnotatedType(RegistrationsHolderImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ContainerObserver.class));
        event.addAnnotatedType(manager.createAnnotatedType(InstanceHolder.class));
        event.addQualifier(OSGiService.class);
    }
    
    public void registerWeldOSGiContexts(@Observes AfterBeanDiscovery event) {
        for (Iterator<Type> iterator = this.servicesToBeInjected.keySet().iterator();
                                                iterator.hasNext();) {
            Type type =  iterator.next();
            if (!(type instanceof Class)) {
                //XXX: need to handle Instance<Class>. This fails currently
                System.out.println("Unknown type:" + type);
                event.addDefinitionError(
                    new UnsupportedOperationException(
                        "Injection target type " + type + "not supported"));
                break; 
            }
            addBean(event, type, this.servicesToBeInjected.get(type));
        }

        for (Iterator<Type> iterator = this.filteredServiceToBeInjected.keySet().iterator();
                                                iterator.hasNext();) {
            Type type =  iterator.next();
            addFilteredService(event, type, this.filteredServiceToBeInjected.get(type));
        }
    }

    public void registerObservers(@Observes ProcessObserverMethod event) {
        Set<Annotation> qualifiers = event.getObserverMethod().getObservedQualifiers();
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Filter.class)) {
                observers.add(qualifier);
            }
        }
    }

    public void afterProcessInjectionTarget(@Observes ProcessInjectionTarget<?> event){
        Set<InjectionPoint> injectionPoints = event.getInjectionTarget().getInjectionPoints();
        discoverServiceInjectionPoints(injectionPoints);
    }

    public void afterProcessBean(@Observes ProcessBean event){
        Set<InjectionPoint> injectionPoints = event.getBean().getInjectionPoints();
        discoverServiceInjectionPoints(injectionPoints);
    }

    private void discoverServiceInjectionPoints(Set<InjectionPoint> injectionPoints) {
        for (Iterator<InjectionPoint> iterator 
                = injectionPoints.iterator(); iterator.hasNext();) {
            InjectionPoint injectionPoint = iterator.next();
            boolean service = false;
            try {
                if (((ParameterizedType)injectionPoint.getType())
                        .getRawType().equals(Service.class)) {
                    service = true;
                }
            } catch (Exception e) {}
            
            Set<Annotation> qualifs = injectionPoint.getQualifiers();
            for (Iterator<Annotation> qualifIter = qualifs.iterator();
                                                    qualifIter.hasNext();) {
                Annotation annotation = qualifIter.next();
                if (annotation.annotationType().equals(OSGiService.class)){
                    if (contains(injectionPoint.getQualifiers(), Required.class)) {
                        requiredOsgiServiceDependencies.add((Class) injectionPoint.getType());
                    }
                    addServiceInjectionInfo(injectionPoint);

                }
                if (annotation.annotationType().equals(Filter.class)){
                    if (service) {
                        addFilteredServiceInjectionInfo(injectionPoint);
                    }
                }
            }
        }
    }

    private void addFilteredServiceInjectionInfo(InjectionPoint injectionPoint) {
        Type key = injectionPoint.getType();
        if (!filteredServiceToBeInjected.containsKey(key)){
            filteredServiceToBeInjected.put(key, new HashSet<InjectionPoint>());
        }
        filteredServiceToBeInjected.get(key).add(injectionPoint);
    }

    private void addServiceInjectionInfo(InjectionPoint injectionPoint) {
        Type key = injectionPoint.getType();
        if (!servicesToBeInjected.containsKey(key)){
            servicesToBeInjected.put(key, new HashSet<InjectionPoint>());
        }
        servicesToBeInjected.get(key).add(injectionPoint);
    }
    
    private void addBean(
            AfterBeanDiscovery event,
            final Type type,
            final Set<InjectionPoint> injectionPoints) {
        
        for (Iterator<InjectionPoint> iterator 
                = injectionPoints.iterator(); iterator.hasNext();) {
            
            final InjectionPoint injectionPoint = iterator.next();
            event.addBean(new OSGiServiceBean(injectionPoint));
        }
    }

    private void addFilteredService(
            AfterBeanDiscovery event,
            final Type type,
            final Set<InjectionPoint> injectionPoints) {

        for (Iterator<InjectionPoint> iterator
                = injectionPoints.iterator(); iterator.hasNext();) {

            final InjectionPoint injectionPoint = iterator.next();
            event.addBean(new FilteredServiceBean(injectionPoint));
        }
    }

    private boolean contains(Set<Annotation> qualifiers, Class<? extends Annotation> qualifier) {
        for (Annotation annotation : qualifiers) {
            if (annotation.annotationType().equals(qualifier)) {
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
