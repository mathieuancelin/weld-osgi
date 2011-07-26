package com.sample.osgi.extension;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.Field;
import java.util.Set;

@ApplicationScoped
public class TestExtension implements Extension {

    <X> void processInjectionTarget(@Observes ProcessInjectionTarget<X> processInjectionTarget) {

        final InjectionTarget<X> old = processInjectionTarget.getInjectionTarget();
        final AnnotatedType<X> annotatedType = processInjectionTarget.getAnnotatedType();

        InjectionTarget<X> wrapped = new InjectionTarget<X>() {
            @Override
            public void inject(X instance, CreationalContext<X> ctx) {
                old.inject(instance, ctx);
                for (Field field : annotatedType.getJavaClass().getFields()) {
                    if (field.getType() == String.class) {
                        try {
                            field.setAccessible(true);
                            field.set(instance, "Hacked by extension !");
                        } catch (Exception e) {
                            throw new InjectionException(e);
                        }
                    }
                }
            }

            @Override
            public void postConstruct(X instance) {
                old.postConstruct(instance);
            }

            @Override
            public void preDestroy(X instance) {
                old.preDestroy(instance);
            }

            @Override
            public X produce(CreationalContext<X> ctx) {
                return old.produce(ctx);
            }

            @Override
            public void dispose(X instance) {
                old.dispose(instance);
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return old.getInjectionPoints();
            }
        };

        processInjectionTarget.setInjectionTarget(wrapped);
    }
}
