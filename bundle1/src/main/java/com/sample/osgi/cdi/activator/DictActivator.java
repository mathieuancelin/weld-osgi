package com.sample.osgi.cdi.activator;

import com.sample.osgi.cdi.services.DictionaryService;
import com.sample.osgi.cdi.services.impl.EnglishDictionaryServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN
 */
public class DictActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(DictionaryService.class.getName(), 
                new EnglishDictionaryServiceImpl(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
