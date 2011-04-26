package com.sample.osgi.api;

import org.jboss.weld.environment.osgi.api.extension.Registrations;
import org.jboss.weld.environment.osgi.api.extension.ServiceRegistry;

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

    Registrations<SomeService> getRegs();

    ServiceRegistry getRegistry();

}
