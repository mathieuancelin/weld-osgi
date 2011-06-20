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
    public ServiceProvider(@OSGiService PropertyService constructorService,
                           @OSGiService @Filter("(Name.value=1)") PropertyService constructorFilteredService,
                           @OSGiService @Name("2") PropertyService constructorQualifiedService,
                           @OSGiService @Name("1") PropertyService constructorQualifiedFromPropertyService,
                           @OSGiService @Filter("(Name.value=2)") PropertyService constructorFilteredFromQualifierService,
                           @OSGiService @Filter("(Name.value=1)") PropertyService constructorOtherFilteredService) {
        this.constructorService = constructorService;
        this.constructorFilteredService = constructorFilteredService;
        this.constructorQualifiedService = constructorQualifiedService;
        this.constructorQualifiedFromPropertyService = constructorQualifiedFromPropertyService;
        this.constructorFilteredFromQualifierService = constructorFilteredFromQualifierService;
        this.constructorOtherFilteredService = constructorOtherFilteredService;
    }

    private PropertyService constructorService;
    private PropertyService constructorFilteredService;
    private PropertyService constructorQualifiedService;
    private PropertyService constructorQualifiedFromPropertyService;
    private PropertyService constructorFilteredFromQualifierService;
    private PropertyService constructorOtherFilteredService;

    private PropertyService initializerService;
    private PropertyService initializerFilteredService;
    private PropertyService initializerQualifiedService;
    private PropertyService initializerQualifiedFromPropertyService;
    private PropertyService initializerFilteredFromQualifierService;
    private PropertyService initializerOtherFilteredService;

    @Inject
    public void setInitializerService(@OSGiService PropertyService initializerService) {
        this.initializerService = initializerService;
    }

    @Inject
    public void setInitializerFilteredService(@OSGiService @Filter("(Name.value=1)") PropertyService initializerFilteredService) {
        this.initializerFilteredService = initializerFilteredService;
    }

    @Inject
    public void setInitializerQualifiedService(@OSGiService @Name("2") PropertyService initializerQualifiedService) {
        this.initializerQualifiedService = initializerQualifiedService;
    }

    @Inject
    public void setInitializerQualifiedFromPropertyService(@OSGiService @Name("1") PropertyService initializerQualifiedFromPropertyService) {
        this.initializerQualifiedFromPropertyService = initializerQualifiedFromPropertyService;
    }

    @Inject
    public void setInitializerFilteredFromQualifierService(@OSGiService @Filter("(Name.value=2)") PropertyService initializerFilteredFromQualifierService) {
        this.initializerFilteredFromQualifierService = initializerFilteredFromQualifierService;
    }

    @Inject
    public void setInitializerOtherFilteredService(@OSGiService @Filter("(Name.value=1)") PropertyService initializerOtherFilteredService) {
        this.initializerOtherFilteredService = initializerOtherFilteredService;
    }

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

    public PropertyService getConstructorService() {
        return constructorService;
    }

    public PropertyService getConstructorFilteredService() {
        return constructorFilteredService;
    }

    public PropertyService getConstructorQualifiedService() {
        return constructorQualifiedService;
    }

    public PropertyService getConstructorQualifiedFromPropertyService() {
        return constructorQualifiedFromPropertyService;
    }

    public PropertyService getConstructorFilteredFromQualifierService() {
        return constructorFilteredFromQualifierService;
    }

    public PropertyService getConstructorOtherFilteredService() {
        return constructorOtherFilteredService;
    }

    public PropertyService getInitializerService() {
        return initializerService;
    }

    public PropertyService getInitializerFilteredService() {
        return initializerFilteredService;
    }

    public PropertyService getInitializerQualifiedService() {
        return initializerQualifiedService;
    }

    public PropertyService getInitializerQualifiedFromPropertyService() {
        return initializerQualifiedFromPropertyService;
    }

    public PropertyService getInitializerFilteredFromQualifierService() {
        return initializerFilteredFromQualifierService;
    }

    public PropertyService getInitializerOtherFilteredService() {
        return initializerOtherFilteredService;
    }

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
}
