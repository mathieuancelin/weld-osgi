package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Required;
import org.osgi.cdi.impl.extension.services.*;
import org.osgi.cdi.impl.integration.InstanceHolder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;
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
    public static ThreadLocal<Long> currentBundle = new ThreadLocal<Long>();

    private HashMap<Type, Set<InjectionPoint>> servicesToBeInjected = new HashMap<Type, Set<InjectionPoint>>();

    private HashMap<Type, Set<InjectionPoint>> serviceProducerToBeInjected = new HashMap<Type, Set<InjectionPoint>>();

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

    public void afterProcessInjectionTarget(@Observes ProcessInjectionTarget<?> event){
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
    
    public void registerWeldOSGiContexts(@Observes AfterBeanDiscovery event) {
        for (Iterator<Type> iterator = this.servicesToBeInjected.keySet().iterator();iterator.hasNext();) {
            Type type =  iterator.next();
            if (!(type instanceof Class)) {
                //TODO: need to handle Instance<Class>. This fails currently
                System.out.println("Unknown type:" + type);
                event.addDefinitionError(
                    new UnsupportedOperationException(
                        "Injection target type " + type + "not supported"));
                break; 
            }
            addService(event, type, this.servicesToBeInjected.get(type));
        }

        for (Iterator<Type> iterator = this.serviceProducerToBeInjected.keySet().iterator(); iterator.hasNext();) {
            Type type =  iterator.next();
            addServiceProducer(event, type, this.serviceProducerToBeInjected.get(type));
        }
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

//    private Set<InjectionPoint> discoverAndProcessServiceInjectionPoints(Set<InjectionPoint> injectionPoints) {
//        Set<InjectionPoint> result = new HashSet<InjectionPoint>();
//        for (Iterator<InjectionPoint> iterator = injectionPoints.iterator(); iterator.hasNext();) {
//            InjectionPoint injectionPoint = iterator.next();
//
//            boolean service = false;
//            try {
//                if (((ParameterizedType) injectionPoint.getType()).getRawType().equals(Service.class)) {
//                    service = true;
//                }
//            } catch (Exception e) {//Not a ParameterizedType, skip
//            }
//
//            if (service) {
//                injectionPoint = processQualifiers(injectionPoint);
//                addServiceProducerInjectionInfo(injectionPoint);
//            } else if (contains(injectionPoint.getQualifiers(), OSGiService.class)) {
//                injectionPoint = processQualifiers(injectionPoint);
//                if (contains(injectionPoint.getQualifiers(), Required.class)) {
//                    requiredOsgiServiceDependencies.add((Class) injectionPoint.getType());
//                }
//                addServiceInjectionInfo(injectionPoint);
//            }
//            result.add(injectionPoint);
//        }
//        return result;
//    }

    public InjectionPoint processQualifiers(InjectionPoint injectionPoint) {
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        Filter filter = new OSGiFilterQualifierType("");
        boolean osgiService = false, required = false;
        for(Iterator<Annotation> iterator = qualifiers.iterator(); iterator.hasNext();) {
            Annotation qualifier = iterator.next();
            if(qualifier.annotationType().equals(Filter.class)) {
                filter = (Filter)qualifier;
            } else if(qualifier.annotationType().equals(OSGiService.class)) {
                osgiService = true;
            } else if(qualifier.annotationType().equals(Required.class)) {
                required = false;
            }
        }
        filter = FilterGenerator.makeFilter(filter,qualifiers.toArray(new Annotation[qualifiers.size()]));
        Set<Annotation> newQualifiers = new HashSet<Annotation>();
        newQualifiers.add(filter);
        if(osgiService) {
            newQualifiers.add(new AnnotationLiteral<OSGiService>() {});
        }
        if(required) {
            newQualifiers.add(new AnnotationLiteral<Required>() {});
        }
        newQualifiers.add(new AnnotationLiteral<Any>() {
        });
        InjectionPoint result = new CDIOSGiInjectionPoint(injectionPoint, newQualifiers, filter, required);
        return result;
    }

    private void addServiceProducerInjectionInfo(InjectionPoint injectionPoint) {
        Type key = injectionPoint.getType();
        if (!serviceProducerToBeInjected.containsKey(key)){
            serviceProducerToBeInjected.put(key, new HashSet<InjectionPoint>());
        }
        serviceProducerToBeInjected.get(key).add(injectionPoint);
    }

    private void addServiceInjectionInfo(InjectionPoint injectionPoint) {
        Type key = injectionPoint.getType();
        if (!servicesToBeInjected.containsKey(key)){
            servicesToBeInjected.put(key, new HashSet<InjectionPoint>());
        }
        servicesToBeInjected.get(key).add(injectionPoint);
    }
    
    private void addService(AfterBeanDiscovery event, final Type type, final Set<InjectionPoint> injectionPoints) {
        for (Iterator<InjectionPoint> iterator = injectionPoints.iterator(); iterator.hasNext();) {
            final InjectionPoint injectionPoint = iterator.next();
            event.addBean(new OSGiServiceBean(injectionPoint));
        }
    }

    private void addServiceProducer(AfterBeanDiscovery event, final Type type, final Set<InjectionPoint> injectionPoints) {
        for (Iterator<InjectionPoint> iterator = injectionPoints.iterator(); iterator.hasNext();) {
            final InjectionPoint injectionPoint = iterator.next();
            event.addBean(new ServiceProducerBean(injectionPoint));
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
