package com.sample.osgi.api;

import org.osgi.cdi.api.extension.Registration;
import org.osgi.cdi.api.extension.ServiceRegistry;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface ServiceBundle2 {

    int filteredtimesCollection(int base, int time);

    int filteredtimesOSGi(int base, int time);

    int filteredtimesProvider(int base, int time);

    int getArrival();

    int getDeparture();

    void fireLong();

    void fireString();

    Registration<SomeService> getRegs();

    ServiceRegistry getRegistry();

}
