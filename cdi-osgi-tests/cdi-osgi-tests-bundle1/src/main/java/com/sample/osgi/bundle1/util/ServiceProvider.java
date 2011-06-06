package com.sample.osgi.bundle1.util;

import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.inject.Inject;

@Publish
public class ServiceProvider {

    @Inject
    @OSGiService
    private PropertyService service;

    @Inject
    private Service<PropertyService> services;

//    @Inject
//    @OSGiService
//    @Filter("Name.value=1")
//    private PropertyService filteredService;
//
//    @Inject @OSGiService @Name("2")
//    private PropertyService qualifiedService;
//
//    @Inject @OSGiService @Name("1")
//    private PropertyService qualifiedFromPropertyService;
//
//    @Inject @OSGiService @Filter("name = 2")
//    private PropertyService filteredFromQualifierService;

    public PropertyService getService() {
        return service;
    }

    public Service<PropertyService> getServices() {
        return services;
    }
}
