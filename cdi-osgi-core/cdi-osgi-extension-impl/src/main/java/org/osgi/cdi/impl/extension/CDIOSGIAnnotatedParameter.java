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

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * CDI-OSGi annotated parameter. Wrap {@link OSGiService} annotated parameters in order to enable CDI-OSGi features.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class CDIOSGIAnnotatedParameter<T> implements AnnotatedParameter<T> {

    private static Logger logger = LoggerFactory.getLogger(CDIOSGIAnnotatedParameter.class);

    AnnotatedParameter parameter;
    Set<Annotation> annotations = new HashSet<Annotation>();
    Filter filter;

    public CDIOSGIAnnotatedParameter(AnnotatedParameter parameter) {
        logger.debug("Creation of a new CDIOSGIAnnotatedParameter wrapping {}", parameter);
        this.parameter = parameter;
        filter = FilterGenerator.makeFilter(parameter.getAnnotations());
        annotations.add(filter);
        //annotations.add(new AnnotationLiteral<OSGiService>() {});
        annotations.add(new OSGiServiceQualifier(parameter.getAnnotation(OSGiService.class).value()));
        if(parameter.getAnnotation(Required.class) != null) {
            annotations.add(new AnnotationLiteral<Required>() {});
        }
        for(Annotation annotation : parameter.getAnnotations()) {
            if(!annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                annotations.add(annotation);
            }
        }
    }

    @Override
    public int getPosition() {
        return parameter.getPosition();
    }

    @Override
    public AnnotatedCallable<T> getDeclaringCallable() {
        return parameter.getDeclaringCallable();
    }

    @Override
    public Type getBaseType() {
        return parameter.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return parameter.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().equals(annotationType)) {
                return (T) annotation;
            }
        }
        return null;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        if(getAnnotation(annotationType) == null) {
            return false;
        }
        return true;
    }
}
