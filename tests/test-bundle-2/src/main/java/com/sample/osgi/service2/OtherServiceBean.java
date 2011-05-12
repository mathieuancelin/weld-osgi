package com.sample.osgi.service2;

import com.sample.osgi.api.DummyService;
import com.sample.osgi.api.ServiceBundle2;
import com.sample.osgi.api.SomeService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.osgi.cdi.api.extension.Registration;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.ServiceRegistry;
import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Publish;
import org.osgi.cdi.api.extension.annotation.Specification;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.extension.events.ServiceArrival;
import org.osgi.cdi.api.extension.events.ServiceDeparture;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Publish
@ApplicationScoped
public class OtherServiceBean implements ServiceBundle2 {

    @Inject @Filter("(value=fake)") Service<DummyService> service;

    @Inject @OSGiService @Filter("(value=fake)") DummyService osgiService;

    @Inject Event<InterBundleEvent> ibEvent;

    @Inject Event<InterBundleEvent> ibEventAll;

    @Inject Registration<SomeService> regs;

    @Inject ServiceRegistry registry;

    private int arrival = 0;
    private int departure = 0;

    @Override
    public void fireString() {
        ibEvent.fire(new InterBundleEvent("Hello"));
    }

    @Override
    public void fireLong() {
        ibEventAll.fire(new InterBundleEvent(new Long(3)));
    }

    public void listenFake(@Observes
            @Specification(DummyService.class) @Filter("(value=fake)") ServiceArrival event) {
        arrival++;
    }

    public void listenFakeDep(@Observes
            @Specification(DummyService.class) @Filter("(value=fake)") ServiceDeparture event) {
        departure++;
    }

    @Override
    public int filteredtimesCollection(int base, int time) {
        int nbr = service.size();
        int total = 0;
        for (DummyService ser : service) {
            total = total + ser.times(base, time);
        }
        return total / nbr;
    }

    @Override
    public int filteredtimesOSGi(int base, int time) {
        return osgiService.times(base, time);
    }

    @Override
    public int filteredtimesProvider(int base, int time) {
        return service.get().times(base, time);
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
    public Registration<SomeService> getRegs() {
        return regs;
    }

    @Override
    public ServiceRegistry getRegistry() {
        return registry;
    }
}
