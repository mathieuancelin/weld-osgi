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

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * CDI-OSGi annotated method. Wrap regular CDI methods in order to enable CDI-OSGi features.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class CDIOSGiAnnotatedMethod<T> implements AnnotatedMethod<T> {

    private static Logger logger = LoggerFactory.getLogger(CDIOSGiAnnotatedMethod.class);

    AnnotatedMethod method;
    List<AnnotatedParameter<T>> parameters = new ArrayList<AnnotatedParameter<T>>();

    public CDIOSGiAnnotatedMethod(AnnotatedMethod<? super T> method) {
        logger.debug("Creation of a new CDIOSGiAnnotatedMethod wrapping {}", method);
        this.method = method;
        for(AnnotatedParameter parameter : method.getParameters()) {
            logger.trace("Processing parameter {}", parameter);
            if(parameter.isAnnotationPresent(OSGiService.class)) {
                parameters.add(new CDIOSGIAnnotatedParameter(parameter));
            } else {
                parameters.add(parameter);
            }
        }
    }

    @Override
    public Method getJavaMember() {
        return method.getJavaMember();
    }

    @Override
    public boolean isStatic() {
        return method.isStatic();
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        return method.getDeclaringType();
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return parameters;
    }

    @Override
    public Type getBaseType() {
        return method.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return method.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return method.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return method.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return method.isAnnotationPresent(annotationType);
    }
}
