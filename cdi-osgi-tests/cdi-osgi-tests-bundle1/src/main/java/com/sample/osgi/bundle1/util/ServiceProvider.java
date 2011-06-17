package com.sample.osgi.bundle1.util;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Publish;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Publish
public class ServiceProvider {

    @Inject
    @OSGiService
    private PropertyService service;

    @Inject
    @OSGiService
    @Filter("(Name.value=1)")
    private PropertyService filteredService;

    @Inject
    @OSGiService
    @Name("2")
    private PropertyService qualifiedService;

    @Inject
    @OSGiService
    @Name("1")
    private PropertyService qualifiedFromPropertyService;

    @Inject
    @OSGiService
    @Filter("(Name.value=2)")
    private PropertyService filteredFromQualifierService;

    @Inject
    @OSGiService
    @Filter("(Name.value=1)")
    private PropertyService otherFilteredService;

//    @Inject
//    private Service<PropertyService> services;
//
//    @Inject
//    @Filter("Name.value=1")
//    private Service<PropertyService> filteredServices;
//
//    @Inject
//    @Name("2")
//    private Service<PropertyService> qualifiedServices;
//
//    @Inject
//    @Name("1")
//    private Service<PropertyService> qualifiedFromPropertyServices;
//
//    @Inject
//    @Filter("Name.value=2")
//    private Service<PropertyService> filteredFromQualifierServices;
//
//    @Inject
//    @Filter("Name.value=1")
//    private Service<PropertyService> otherFilteredServices;

    public PropertyService getService() {
        return service;
    }

    public PropertyService getFilteredService() {
        return filteredService;
    }

    public PropertyService getQualifiedService() {
        return qualifiedService;
    }

    public PropertyService getQualifiedFromPropertyService() {
        return qualifiedFromPropertyService;
    }

    public PropertyService getFilteredFromQualifierService() {
        return filteredFromQualifierService;
    }

    public PropertyService getOtherFilteredService() {
        return otherFilteredService;
    }

//    public Service<PropertyService> getServices() {
//        return services;
//    }
//
//    public Service<PropertyService> getFilteredServices() {
//        return filteredServices;
//    }
//
//    public Service<PropertyService> getQualifiedServices() {
//        return qualifiedServices;
//    }
//
//    public Service<PropertyService> getQualifiedFromPropertyServices() {
//        return qualifiedFromPropertyServices;
//    }
//
//    public Service<PropertyService> getFilteredFromQualifierServices() {
//        return filteredFromQualifierServices;
//    }
//
//    public Service<PropertyService> getOtherFilteredServices() {
//        return otherFilteredServices;
//    }
}
