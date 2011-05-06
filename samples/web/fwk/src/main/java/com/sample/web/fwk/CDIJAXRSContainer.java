package com.sample.web.fwk;

import com.sample.web.fwk.api.Controller;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;

public class CDIJAXRSContainer extends ServletContainer {

    private static final long serialVersionUID = 1931878850157940335L;

    private WebApplication webapp;

    private final Instance<Object> controllers;

    private Set<Class<?>> classes = new HashSet<Class<?>>();

    @Inject
    public CDIJAXRSContainer(@Any Instance<Object> controllers) {
        this.controllers = controllers;
        classes.add(JerseyApplication.class);
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig)
            throws ServletException {
        return new DefaultResourceConfig();
    }

    @Override
    protected void initiate(ResourceConfig config, WebApplication webapp) {
        this.webapp = webapp;
        for (Controller c : controllers.select(Controller.class)) {
            classes.add(c.getClass());
        }
        webapp.initiate(config, new CDIBeansProvider());
    }

    public WebApplication getWebApplication() {
        return this.webapp;
    }

    public class CDIBeansProvider implements IoCComponentProviderFactory {

        @Override
        public IoCComponentProvider getComponentProvider(final Class<?> type) {
            return getComponentProvider(null, type);
        }

        @Override
        public IoCComponentProvider getComponentProvider(ComponentContext cc, final Class<?> type) {
            try {
                if (classes.contains(type)) {
                    return new CDIComponentprovider(cc, type);
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    public class CDIComponentprovider implements IoCInstantiatedComponentProvider {

        private final ComponentContext cc;
        private final Class<?> type;

        public CDIComponentprovider(ComponentContext cc, Class<?> type) {
            this.cc = cc;
            this.type = type;
        }

        @Override
        public Object getInjectableInstance(Object o) {
            return o;
        }

        @Override
        public Object getInstance() {
            if (type.equals(JerseyApplication.class)) {
                JerseyApplication app = new JerseyApplication();
                app.setClasses(classes);
                return app;
            }
            return controllers.select(type).get();
        }
    }
}
