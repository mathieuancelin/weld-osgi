package com.sample.osgi.bundle3.impl;

import com.sample.osgi.bundle1.api.AutoPublishedService;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
public class AutoPublishedServiceImpl implements AutoPublishedService {
    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
