package com.sample.osgi.bundle1;

import com.sample.osgi.bundle1.api.ManualPublishedService;
import com.sample.osgi.bundle1.impl.ManualPublishedServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator{

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(ManualPublishedService.class.getName(),new ManualPublishedServiceImpl(),null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
