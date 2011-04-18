/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sample.osgi.api;

import org.jboss.weld.environment.osgi.api.extension.ServiceRegistry;

/**
 *
 * @author mathieuancelin
 */
public interface ServiceBundle1 {
    
    public int getArrival();

    public int getDeparture();

    public DummyService getDummy();

    public int getDummyArrival();

    public int getDummyDeparture();

    public ServiceRegistry getRegistry();

    public int getStart();

    public int getStop();
    
    public int getInvalid();

    public int getValid();

    int timesCollection(int base, int time);

    int timesOSGi(int base, int time);

    int timesProvider(int base, int time);

//    int filteredtimesCollection(int base, int time);
//
//    int filteredtimesOSGi(int base, int time);
//
//    int filteredtimesProvider(int base, int time);

}
