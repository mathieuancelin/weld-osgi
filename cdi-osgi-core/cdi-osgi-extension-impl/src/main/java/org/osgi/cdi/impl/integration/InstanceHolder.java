package org.osgi.cdi.impl.integration;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

public class InstanceHolder {

    @Inject @Any Instance<Object> instance;

    public <T> Instance<T> select(Class<T> clazz, Annotation... annotations) {
        return instance.select(clazz,annotations);
    }

    public <T> Instance<T> select(TypeLiteral<T> type, Annotation... annotations) {
        return instance.select(type, annotations);
    }

}
