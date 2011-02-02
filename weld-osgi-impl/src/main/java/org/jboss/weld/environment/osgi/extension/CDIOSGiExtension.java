package org.jboss.weld.environment.osgi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;
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
import javax.enterprise.util.AnnotationLiteral;
import org.jboss.weld.environment.osgi.api.extension.OSGiService;

import org.jboss.weld.environment.osgi.api.extension.Publish;
import org.jboss.weld.environment.osgi.extension.services.DynamicServiceHandler;
import org.jboss.weld.environment.osgi.extension.services.ServiceImpl;
import org.jboss.weld.environment.osgi.extension.services.ServicesImpl;
import org.jboss.weld.environment.osgi.extension.services.ServicesProducer;
import org.jboss.weld.environment.osgi.integration.ShutdownManager;
import org.jboss.weld.util.collections.ArraySet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Weld OSGi extension.
 *
 * Contains copy/paste parts from the GlasFish OSGI-CDI extension.
 * 
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class CDIOSGiExtension implements Extension {

    private HashMap<Type, Set<InjectionPoint>> servicesToBeInjected
                            = new HashMap<Type, Set<InjectionPoint>>();

    public void registerWeldOSGiBeans(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        event.addAnnotatedType(manager.createAnnotatedType(ServicesProducer.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServicesImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ServiceImpl.class));
        event.addAnnotatedType(manager.createAnnotatedType(ShutdownManager.class));
        event.addQualifier(OSGiService.class);

    }
    // TODO : add injection for service registry, context, bundle, log service, entreprise stuff

    public void registerWeldOSGiContexts(@Observes
                                         AfterBeanDiscovery event,
                                         BeanManager manager) {
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

    public void registerService(@Observes ProcessBean event, BeanManager manager) {
        Bean<?> bean = event.getBean();
        if (isAnnotatedWith(bean, Publish.class)) {

            Class<?> clazz = bean.getBeanClass();
            BundleContext bundleContext = FrameworkUtil.getBundle(clazz).getBundleContext();
            Set<Class<?>> types = findServiceTypes(clazz);

            registerService(bean, bundleContext, types, manager);
        }
    }

    private void registerService(Bean bean,
                                 BundleContext bundleContext,
                                 Set<Class<?>> types,
                                 BeanManager manager) {

        String[] interfaces = convertToStringArray(types);
        CreationalContext<?> cc = manager.createCreationalContext(bean);
        Object instance = bean.create(cc);
        bundleContext.registerService(interfaces, instance, null);
    }

    private String[] convertToStringArray(Set<Class<?>> types) {
        List<String> strings = new ArrayList<String>(types.size());
        for (Class<?> type : types) {
            strings.add(type.getName());
        }
        return strings.toArray(new String[strings.size()]);
    }

    private Set<Class<?>> findServiceTypes(Class<?> clazz) {
        Set<Class<?>> types = new ArraySet<Class<?>>();

        Publish publish = clazz.getAnnotation(Publish.class);
        if (publish.contracts().length == 0) {
            // No types specified, we have to discover them
            discoverInterfaces(clazz, types);
        } else {
            // Types specified by annotation
            for (Class<?> specification : publish.contracts()) {
                types.add(specification);
            }
        }

        return types;
    }

    private void discoverInterfaces(Class<?> clazz, Set<Class<?>> types) {
        Class<?> traversed = clazz;
        while (!Object.class.equals(traversed)) {
            // Traverse each super type
            for (Class<?> type : traversed.getInterfaces()) {
                types.add(type);
            }

            traversed = traversed.getSuperclass();
        }
    }

    private boolean isAnnotatedWith(Bean<?> bean, Class<? extends Annotation> annotation) {
        Set<Annotation> qualifiers = bean.getQualifiers();
        for (Annotation qualifier : qualifiers ) {
            if (annotation.equals(qualifier)) {
                return true;
            }
        }
        return false;
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

    private final class OSGiServiceBean implements Bean {
        
        private final Type type;
        private final InjectionPoint injectionPoint;

        private OSGiServiceBean(InjectionPoint injectionPoint) {
            this.injectionPoint = injectionPoint;
            this.type = this.injectionPoint.getType();
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
                            new DynamicServiceHandler(bundle, serviceName));
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
}
