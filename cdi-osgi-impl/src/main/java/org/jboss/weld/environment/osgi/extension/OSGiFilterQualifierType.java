package org.jboss.weld.environment.osgi.extension;

import javax.enterprise.util.AnnotationLiteral;
import org.jboss.weld.environment.osgi.api.extension.annotation.Filter;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiFilterQualifierType 
        extends AnnotationLiteral<Filter>
        implements Filter {

    private final String value;

    protected OSGiFilterQualifierType(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
