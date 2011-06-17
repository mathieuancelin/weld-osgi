package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish//(properties = {
//        @Property(name = "Name.value",value = "1")
//})
@Name("1")
public class PropertyServiceImpl2 implements PropertyService {

    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
