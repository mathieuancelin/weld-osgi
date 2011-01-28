package org.jboss.weld.environment.osgi.extension.events;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Observes;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.weld.environment.osgi.api.extension.Filter;
import org.jboss.weld.environment.osgi.api.extension.Observer;

@Interceptor @Observer
public class ObserverInterceptor {

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Throwable {
        System.out.println("intercept");
        boolean observes = false;
        Filter filter = null;
        Annotation[][] parametersAnnotations = ctx.getMethod().getParameterAnnotations();
        for(int i=0; i < parametersAnnotations.length; i++) {
            for (int j=0; j < parametersAnnotations[i].length; j++) {
                Annotation annotation = parametersAnnotations[i][j];
                if (annotation.annotationType().equals(Observes.class)) {
                    observes = true;
                }
                if (annotation.annotationType().equals(Filter.class)) {
                    filter = (Filter) annotation;
                }
            }
        }
        if (observes && filter != null) {
            Object event = ctx.getParameters()[0];
            String filterValue = filter.value();
            if (!event.getClass().getName().contains(filterValue)) {
                return Void.TYPE;
            } else {
                return ctx.proceed();
            }
        }
        return ctx.proceed();
    }
}
