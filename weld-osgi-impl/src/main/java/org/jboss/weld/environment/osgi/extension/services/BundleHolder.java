package org.jboss.weld.environment.osgi.extension.services;

import javax.enterprise.context.ApplicationScoped;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class BundleHolder {

    private Bundle bundle;
    
    private BundleContext context;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }
}
