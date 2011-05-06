package com.sample.web.france;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@ApplicationScoped
public class FranceHotelProvider implements HotelProvider {

    @Override
    public Collection<Hotel> hotels() {
        Collection<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("Au bon Hotel", "Paris", "France"));
        hotels.add(new Hotel("Hotel California", "Paris", "France"));
        hotels.add(new Hotel("Hotel Claridge", "Paris", "France"));
        return hotels;
    }

    @Override
    public String getCountry() {
        return "France";
    }
}
