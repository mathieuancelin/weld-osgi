package com.sample.osgi.standalone;

import org.jboss.weld.environment.osgi.embedded.WeldOSGi;
import org.jboss.weld.environment.se.WeldContainer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class StandaloneActivator implements BundleActivator {

    private WeldContainer container;
    private WeldOSGi weld;

    @Override
    public void start(BundleContext context) throws Exception {
        weld = new WeldOSGi(context);
        container = weld.initialize();
        MyService service = container.instance().select(MyService.class).get();
        System.out.println(service.hello());
        System.out.println(service.admin());
        System.out.println(service.adminAvailable());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        weld.shutdown();
    }
}
