package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@Name("2")
public class PropertyServiceImpl3 implements PropertyService {

    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
