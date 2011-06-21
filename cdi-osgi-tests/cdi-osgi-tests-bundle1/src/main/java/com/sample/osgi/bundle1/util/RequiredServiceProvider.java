package com.sample.osgi.bundle1.util;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.RequiredService;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Required;

import javax.inject.Inject;

public class RequiredServiceProvider {

    @Inject
    @OSGiService
    @Required
    @Name("1")
    RequiredService requiredService1;

    @Inject
    @OSGiService
    @Required
    @Name("2")
    RequiredService requiredService2;

    public RequiredService getRequiredService1() {
        return requiredService1;
    }

    public RequiredService getRequiredService2() {
        return requiredService2;
    }
}
