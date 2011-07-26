package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.PersonalizedHashCodeService;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
public class PersonalizedHashCodeServiceImpl implements PersonalizedHashCodeService {

    @Override
    public int hashCode() {
        return 42;
    }
}
