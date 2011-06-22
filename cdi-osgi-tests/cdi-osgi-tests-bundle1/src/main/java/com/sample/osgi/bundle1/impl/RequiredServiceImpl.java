package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.RequiredService;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@Name("1")
public class RequiredServiceImpl implements RequiredService{

    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
