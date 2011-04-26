package org.jboss.weld.environment.osgi.extension.services;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.weld.environment.osgi.api.extension.BundleState;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class BundleHolder {

    private BundleState state = BundleState.INVALID;

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

    public BundleState getState() {
        return state;
    }

    public void setState(BundleState state) {
        this.state = state;
    }
}
