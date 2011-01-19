package com.sample.osgi.cdi.activator;

import com.sample.osgi.cdi.services.DictionaryService;
import com.sample.osgi.cdi.services.SpellCheckerService;
import com.sample.osgi.cdi.services.impl.SpellCheckerServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN
 */
public class SpellActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        SpellCheckerServiceImpl service = new SpellCheckerServiceImpl();
        service.setDictionaryService((DictionaryService) context.getService(context.getServiceReference(DictionaryService.class.getName())));
        context.registerService(SpellCheckerService.class.getName(), service, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
