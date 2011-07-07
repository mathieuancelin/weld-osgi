package com.sample.osgi.bundle1.util;

import org.osgi.cdi.api.extension.annotation.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;

@Publish
public class BundleProvider {

    @Inject
    Bundle bundle;

    @Inject
    BundleContext bundleContext;

    @Inject @BundleHeaders
    Map<String,String> metadata;

    @Inject @BundleHeader("Bundle-SymbolicName")
    String symbolicName;

    @Inject @BundleDataFile("test.txt")
    File file;

    public Bundle getBundle() {
        return bundle;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public File getFile() {
        return file;
    }

    @Inject @BundleName("com.sample.osgi.cdi-osgi-tests-bundle2")
    Bundle bundle2;

    @Inject @BundleName("com.sample.osgi.cdi-osgi-tests-bundle2")
    BundleContext bundleContext2;

    @Inject @BundleName("com.sample.osgi.cdi-osgi-tests-bundle2") @BundleHeaders
    Map<String,String> metadata2;

    @Inject @BundleName("com.sample.osgi.cdi-osgi-tests-bundle2") @BundleHeader("Bundle-SymbolicName")
    String symbolicName2;

    @Inject @BundleName("com.sample.osgi.cdi-osgi-tests-bundle2") @BundleDataFile("test.txt")
    File file2;

    public Bundle getBundle2() {
        return bundle2;
    }

    public BundleContext getBundleContext2() {
        return bundleContext2;
    }

    public Map<String, String> getMetadata2() {
        return metadata2;
    }

    public String getSymbolicName2() {
        return symbolicName2;
    }

    public File getFile2() {
        return file2;
    }
}
