package org.jboss.weld.environment.osgi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.enterprise.util.AnnotationLiteral;
import org.jboss.weld.environment.osgi.api.extension.Filter;
import org.jboss.weld.environment.osgi.api.extension.OSGiService;
import org.jboss.weld.environment.osgi.extension.services.BundleHolder;

import org.jboss.weld.environment.osgi.extension.services.DynamicServiceHandler;
import org.jboss.weld.environment.osgi.extension.services.RegistrationsHolder;
import org.jboss.weld.environment.osgi.extension.services.ServiceImpl;
import org.jboss.weld.environment.osgi.extension.services.ServiceRegistryImpl;
import org.jboss.weld.environment.osgi.extension.services.ServicesImpl;
import org.jboss.weld.environment.osgi.extension.services.WeldOSGiProducer;
import org.jboss.weld.environment.osgi.integration.ShutdownManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Weld OSGi extension.
 *
 * Contains copy/paste parts from the GlasFish OSGI-CDI extension.
 * 
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class CDIOSGiExtension implements Extension {

    private HashMap<Type, Set<InjectionPoint>> servicesToBeInjected
                            = new HashMap<Type, Set<InjectionPoint>>();

    private List<Annotation> observers = new ArrayList<Annotation>();

    public void registerWeldOSGiBeans(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        event.addAnnotatedType(manager.createAnnotatedType(WeldOSGiProducer.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServicesImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServiceImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ShutdownManager.class));
        event.addAnnotatedType(manager.createAnnotatedType(BundleHolder.class));
        event.addAnnotatedType(manager.createAnnotatedType(RegistrationsHolder.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryImpl.class));
        event.addQualifier(OSGiService.class);
    }
    
    // TODO : add injection for service registry, context, bundle, log service, entreprise stuff
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
            Set<Annotation> qualifs = injectionPoint.getQualifiers();
            for (Iterator<Annotation> qualifIter = qualifs.iterator();
                                                    qualifIter.hasNext();) {
                Annotation annotation = qualifIter.next();
                if (annotation.annotationType().equals(OSGiService.class)){
                    addServiceInjectionInfo(injectionPoint);
                }
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

    public List<Annotation> getObservers() {
        return observers;
    }

    private final class OSGiServiceBean implements Bean {
        
        private final Type type;
        private final InjectionPoint injectionPoint;
        private Filter filter;

        private OSGiServiceBean(InjectionPoint injectionPoint) {
            this.injectionPoint = injectionPoint;
            this.type = this.injectionPoint.getType();
            Set<Annotation> qualifiers = injectionPoint.getQualifiers();
            for (Annotation qualifier : qualifiers) {
                if (qualifier.annotationType().equals(Filter.class)) {
                    filter = (Filter) qualifier;
                    break;
                }
            }
        }

        @Override
        public Object create(CreationalContext ctx) {
            try {
                Type serviceType = injectionPoint.getType();
                Class serviceClass = ((Class)(serviceType));
                String serviceName = serviceClass.getName();
                Bundle bundle = FrameworkUtil.getBundle(
                                    injectionPoint.getMember().getDeclaringClass());
                return Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class[]{(Class) serviceClass},
                            new DynamicServiceHandler(bundle, serviceName, filter));
            } catch (Exception e) {
                throw new CreationException(e);
            }
        }

        @Override
        public void destroy(Object instance,
                CreationalContext creationalContext) {
            // Nothing to do, services are unget after each call.
        }

        @Override
        public Class getBeanClass() {
            return (Class)type;
        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
          return Collections.emptySet();
        }

        @Override
        public String getName() {
            return type.toString();
        }

        @Override
        public Set<Annotation> getQualifiers() {
            Set<Annotation> s = new HashSet<Annotation>();
            s.add(new AnnotationLiteral<Default>() {});
            s.add(new AnnotationLiteral<Any>() {});
            s.add(new OSGiServiceQualifierType());
            if (filter != null) {
                s.add(new OSGiFilterQualifierType(filter.value()));
            }
            return s;
        }

        @Override
        public Class<? extends Annotation> getScope() {
            return Dependent.class;
        }

        @Override
        public Set<Class<? extends Annotation>> getStereotypes() {
            return Collections.emptySet();
        }

        @Override
        public Set<Type> getTypes() {
            Set<Type> s = new HashSet<Type>();
            s.add(type);
            s.add(Object.class);
            return s;
        }

        @Override
        public boolean isAlternative() {
            return false;
        }

        @Override
        public boolean isNullable() {
            return false;
        }
    }

    private final class OSGiServiceQualifierType
        extends AnnotationLiteral<OSGiService>
            implements OSGiService {
    }

    private final class OSGiFilterQualifierType
        extends AnnotationLiteral<Filter>
            implements Filter {

        private final String value;

        public OSGiFilterQualifierType(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }
}
