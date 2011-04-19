package com.sample.osgi.service1;

import com.sample.osgi.api.DummyService;
import javax.inject.Named;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Named
public class DummyServiceImpl implements DummyService {

    @Override
    public int times(int base, int time) {
        return base * time;
    }

}
