package com.sample.osgi.extension;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@Name("extension")
public class ServiceExtension implements PropertyService {

    private String name;

    public ServiceExtension() {
        name = getClass().getName();
    }

    @Override
    public String whoAmI() {
        return name;
    }
}
