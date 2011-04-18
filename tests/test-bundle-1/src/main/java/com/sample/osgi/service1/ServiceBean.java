package com.sample.osgi.service1;

import com.sample.osgi.api.DummyService;
import com.sample.osgi.api.ServiceBundle1;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.jboss.weld.environment.osgi.api.extension.ServiceRegistry;
import org.jboss.weld.environment.osgi.api.extension.Services;
import org.jboss.weld.environment.osgi.api.extension.annotation.Filter;
import org.jboss.weld.environment.osgi.api.extension.annotation.OSGiService;
import org.jboss.weld.environment.osgi.api.extension.annotation.Publish;
import org.jboss.weld.environment.osgi.api.extension.annotation.Required;
import org.jboss.weld.environment.osgi.api.extension.annotation.Specification;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.events.BundleContainerShutdown;
import org.jboss.weld.environment.osgi.api.extension.events.Invalid;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceArrival;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceDeparture;
import org.jboss.weld.environment.osgi.api.extension.events.Valid;

/**
 *
 * @author mathieuancelin
 */
@Publish
@ApplicationScoped
public class ServiceBean implements ServiceBundle1 {

    @Inject ServiceRegistry registry;

    @Inject DummyServiceImpl dummy;

    @Inject @Required Service<DummyService> service;

    @Inject @Required Services<DummyService> services;

    @Inject @OSGiService DummyService osgiService;

    private int start = 0;
    private int stop = 0;

    private int arrival = 0;
    private int departure = 0;

    private int dummyArrival = 0;
    private int dummyDeparture = 0;

    private int valid = 0;
    private int invalid = 0;

    public void start(@Observes BundleContainerInitialized event) {
        start++;
    }

    public void stop(@Observes BundleContainerShutdown event) {
        stop++;
    }

    public void bindService(@Observes ServiceArrival event) {
        arrival++;
    }

    public void unbindService(@Observes ServiceDeparture event) {
        departure++;
    }

    public void bindDummyService(@Observes @Specification(DummyService.class) ServiceArrival event) {
        dummyArrival++;
    }

    public void unbindDummyService(@Observes @Specification(DummyService.class) ServiceDeparture event) {
        dummyDeparture++;
    }

    public void valid(@Observes Valid event) {
        valid++;
    }

    public void invalid(@Observes Invalid event) {
        invalid++;
    }

    @Override
    public int timesProvider(int base, int time) {
        return service.get().times(base, time);
    }

    @Override
    public int timesCollection(int base, int time) {
        int nbr = services.size();
        int total = 0;
        for (DummyService ser : services) {
            total = total + ser.times(base, time);
        }
        return total / nbr;
    }

    @Override
    public int timesOSGi(int base, int time) {
        return osgiService.times(base, time);
    }

    @Override
    public int getArrival() {
        return arrival;
    }

    @Override
    public int getDeparture() {
        return departure;
    }

    @Override
    public DummyServiceImpl getDummy() {
        return dummy;
    }

    @Override
    public int getDummyArrival() {
        return dummyArrival;
    }

    @Override
    public int getDummyDeparture() {
        return dummyDeparture;
    }

    @Override
    public ServiceRegistry getRegistry() {
        return registry;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getStop() {
        return stop;
    }

    @Override
    public int getInvalid() {
        return invalid;
    }

    @Override
    public int getValid() {
        return valid;
    }
}
