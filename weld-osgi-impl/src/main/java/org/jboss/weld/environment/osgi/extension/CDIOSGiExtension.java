package org.jboss.weld.environment.osgi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import org.jboss.weld.environment.osgi.api.extension.annotation.Filter;
import org.jboss.weld.environment.osgi.api.extension.annotation.OSGiService;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.jboss.weld.environment.osgi.api.extension.Services;
import org.jboss.weld.environment.osgi.api.extension.annotation.Required;
import org.jboss.weld.environment.osgi.extension.services.BundleHolder;
import org.jboss.weld.environment.osgi.extension.services.ContainerObserver;

import org.jboss.weld.environment.osgi.extension.services.RegistrationsHolder;
import org.jboss.weld.environment.osgi.extension.services.ServiceImpl;
import org.jboss.weld.environment.osgi.extension.services.ServiceRegistryImpl;
import org.jboss.weld.environment.osgi.extension.services.ServicesImpl;
import org.jboss.weld.environment.osgi.extension.services.WeldOSGiProducer;

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

    private HashMap<Type, Set<InjectionPoint>> filteredServicesToBeInjected
                            = new HashMap<Type, Set<InjectionPoint>>();

    private HashMap<Type, Set<InjectionPoint>> filteredServiceToBeInjected
                            = new HashMap<Type, Set<InjectionPoint>>();

    private List<Annotation> observers = new ArrayList<Annotation>();

    private Set<Class<?>> requiredOsgiServiceDependencies = new HashSet<Class<?>>();

    public void registerWeldOSGiBeans(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        event.addAnnotatedType(manager.createAnnotatedType(WeldOSGiProducer.class));
        event.addAnnotatedType(manager.createAnnotatedType(BundleHolder.class));
        event.addAnnotatedType(manager.createAnnotatedType(RegistrationsHolder.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ContainerObserver.class));
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

        for (Iterator<Type> iterator = this.filteredServicesToBeInjected.keySet().iterator();
                                                iterator.hasNext();) {
            Type type =  iterator.next();
            addFilteredServices(event, type, this.filteredServicesToBeInjected.get(type));
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
            boolean services = false;
            boolean service = false;
            try {
                if (((ParameterizedType)injectionPoint.getType())
                        .getRawType().equals(Services.class)) {
                    services = true;
                } else if (((ParameterizedType)injectionPoint.getType())
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
                    if (services) {
                        addFilteredServicesInjectionInfo(injectionPoint);
                    }
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

    private void addFilteredServicesInjectionInfo(InjectionPoint injectionPoint) {
        Type key = injectionPoint.getType();
        if (!filteredServicesToBeInjected.containsKey(key)){
            filteredServicesToBeInjected.put(key, new HashSet<InjectionPoint>());
        }
        filteredServicesToBeInjected.get(key).add(injectionPoint);
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

    private void addFilteredServices(
            AfterBeanDiscovery event,
            final Type type,
            final Set<InjectionPoint> injectionPoints) {

        for (Iterator<InjectionPoint> iterator
                = injectionPoints.iterator(); iterator.hasNext();) {

            final InjectionPoint injectionPoint = iterator.next();
            event.addBean(new FilteredServicesBean(injectionPoint));
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
