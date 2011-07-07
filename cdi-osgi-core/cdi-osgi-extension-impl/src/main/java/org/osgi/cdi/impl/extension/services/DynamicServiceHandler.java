package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class DynamicServiceHandler implements InvocationHandler {

    private final Bundle bundle;
    private final String name;
    private Filter filter;
    private final ServiceTracker tracker;
    private final long timeout;

    public DynamicServiceHandler(Bundle bundle, String name, Filter filter, OSGiService anno) {
        this.bundle = bundle;
        this.name = name;
        this.filter = filter;
        timeout = anno.value();
        try {
//            ServiceReference reference = null;
            if (filter != null && filter.value() != null && filter.value().length() > 0) {
//                ServiceReference[] refs =
//                            bundle.getBundleContext().getServiceReferences(name, filter.value());
//                if (refs != null && refs.length > 0) {
//                    reference = refs[0];
//                }
                this.tracker = new ServiceTracker(bundle.getBundleContext(),
                    bundle.getBundleContext().createFilter(
                        "(&(objectClass=" + name + ")" + filter.value() + ")"),
                    null);
            } else {
//                reference = bundle.getBundleContext().getServiceReference(name);
                this.tracker = new ServiceTracker(bundle.getBundleContext(), name, null);
            }
//            this.tracker = new ServiceTracker(bundle.getBundleContext(), reference, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.tracker.open();
    }

    public void closeHandler() {
        this.tracker.close();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        Object instanceToUse = this.tracker.waitForService(timeout);
        try {
            return method.invoke(instanceToUse, args);
        } catch(Throwable t) {
            throw new RuntimeException(t);
        } finally {
            CDIOSGiExtension.currentBundle.remove();
        }
    }
}
