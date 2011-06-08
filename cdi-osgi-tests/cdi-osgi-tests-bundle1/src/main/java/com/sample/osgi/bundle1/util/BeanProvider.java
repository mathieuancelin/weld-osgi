package com.sample.osgi.bundle1.util;


import org.osgi.cdi.api.extension.annotation.Publish;

import javax.inject.Inject;

@Publish
public class BeanProvider {

    @Inject
    ServiceProviderBean serviceProviderBean;

    public ServiceProviderBean getServiceProviderBean() {
        return serviceProviderBean;
    }
}
