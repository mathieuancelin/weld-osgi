package com.sample.osgi.extension;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.inject.Inject;

@Publish
public class ServiceExtensionProvider {

    @Inject
    @Name("extension")
    private PropertyService serviceExtension;

    @Inject
    @OSGiService
    @Name("extension")
    private PropertyService serviceExtensionService;

    public PropertyService getServiceExtension() {
        return serviceExtension;
    }

    public PropertyService getServiceExtensionService() {
        return serviceExtensionService;
    }
}
