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

import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * CDI-OSGi annotated constructor. Wrap regular CDI constructors in order to enable CDI-OSGi features.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class CDIOSGiAnnotatedConstructor<T> implements AnnotatedConstructor<T> {

    private static Logger logger = LoggerFactory.getLogger(CDIOSGiAnnotatedConstructor.class);

    AnnotatedConstructor constructor;
    List<AnnotatedParameter<T>> parameters = new ArrayList<AnnotatedParameter<T>>();

    public CDIOSGiAnnotatedConstructor(AnnotatedConstructor<T> constructor) {
        logger.debug("Creation of a new CDIOSGiAnnotatedConstructor wrapping {}", constructor);
        this.constructor = constructor;
        for(AnnotatedParameter parameter : constructor.getParameters()) {
            logger.trace("Processing parameter {}", parameter);
            if(parameter.isAnnotationPresent(OSGiService.class)) {
                parameters.add(new CDIOSGIAnnotatedParameter(parameter));
            } else {
                parameters.add(parameter);
            }
        }
    }

    @Override
    public Constructor<T> getJavaMember() {
        return constructor.getJavaMember();
    }

    @Override
    public boolean isStatic() {
        return constructor.isStatic();
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        return constructor.getDeclaringType();
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return parameters;
    }

    @Override
    public Type getBaseType() {
        return constructor.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return constructor.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return constructor.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return constructor.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return constructor.isAnnotationPresent(annotationType);
    }
}
