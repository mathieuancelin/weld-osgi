package com.sample.web.spain;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@ApplicationScoped
public class SpainHotelProvider implements HotelProvider {

    @Override
    public Collection<Hotel> hotels() {
        Collection<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("Catalonia Plaza Mayor", "Madrid", "Spain"));
        hotels.add(new Hotel("emperador", "Madrid", "Spain"));
        hotels.add(new Hotel("Il Castillas hotel", "Madrid", "Spain"));
        hotels.add(new Hotel("Ada Palace", "Madrid", "Spain"));
        hotels.add(new Hotel("Palafox Central Suites", "Madrid", "Spain"));
        return hotels;
    }

    @Override
    public String getCountry() {
        return "Spain";
    }
}
