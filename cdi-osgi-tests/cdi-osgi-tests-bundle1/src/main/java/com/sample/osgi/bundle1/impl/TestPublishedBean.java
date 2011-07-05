package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.Name2;
import com.sample.osgi.bundle1.api.PropertyService;
import com.sample.osgi.bundle1.api.TestPublished;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Publish
@ApplicationScoped
public class TestPublishedBean implements TestPublished {

    @Inject @OSGiService @Filter("(Name.value=2)") PropertyService  service;

    @Inject @OSGiService @Name2 PropertyService  service2;

    public PropertyService getService() {
        return service;
    }

    public PropertyService getService2() {
        return service2;
    }
}
