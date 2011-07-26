package com.sample.osgi.standalone;

import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.service.packageadmin.PackageAdmin;

import javax.inject.Inject;

public class MyService {

    @Inject @OSGiService
    private PackageAdmin admin;

    @Inject
    private Service<PackageAdmin> adminService;

    public PackageAdmin admin() {
        return admin;
    }

    public int adminAvailable() {
        return adminService.size();
    }

    public String hello() {
        return "Hello";
    }
}
