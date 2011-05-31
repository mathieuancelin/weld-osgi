package com.sample.osgi.bundle2.impl;

import com.sample.osgi.bundle1.api.ManualPublishedService;

public class ManualPublishedServiceImpl implements ManualPublishedService {


    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
