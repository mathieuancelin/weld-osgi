package org.apache.openwebbeans.environment.osgi.integration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class InstanceHolder {

    @Inject Instance<Object> instance;

    public Instance<Object> getInstance() {
        return instance;
    }

}
