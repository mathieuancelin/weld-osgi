package com.sample.web.england;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@ApplicationScoped
public class EnglandHotelProvider implements HotelProvider {

    @Override
    public Collection<Hotel> hotels() {
        Collection<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("The Montcalm", "London", "England"));
        hotels.add(new Hotel("The Berkeley", "London", "England"));
        return hotels;
    }

    @Override
    public String getCountry() {
        return "England";
    }
}
