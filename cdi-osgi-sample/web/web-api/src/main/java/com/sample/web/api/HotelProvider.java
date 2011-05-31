package com.sample.web.api;

import java.util.Collection;

public interface HotelProvider {

    String getCountry();

    Collection<Hotel> hotels();
}
