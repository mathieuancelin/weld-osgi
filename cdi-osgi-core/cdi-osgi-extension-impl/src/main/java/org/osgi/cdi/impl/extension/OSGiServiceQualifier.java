package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.OSGiService;

import javax.enterprise.util.AnnotationLiteral;

public class OSGiServiceQualifier
        extends AnnotationLiteral<OSGiService>
        implements OSGiService {

    private final int timeout;

    public OSGiServiceQualifier(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int value() {
        return timeout;
    }
}
