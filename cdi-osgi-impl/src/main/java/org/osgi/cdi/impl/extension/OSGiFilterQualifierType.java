package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;

import javax.enterprise.util.AnnotationLiteral;

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
