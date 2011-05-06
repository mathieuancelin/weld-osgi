package com.sample.web.app;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import com.sample.web.fwk.api.Controller;
import com.sample.web.fwk.view.Render;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Required;

@Path("hotels")
public class HotelController implements Controller {

    @Inject @Required Service<HotelProvider> providers;
    
    @Inject App app;

    @GET
    @Path("all")
    public Response all() {
        List<Hotel> hotels = new ArrayList<Hotel>();
        for (HotelProvider provider : providers) {
            hotels.addAll(provider.hotels());
        }
        if (app.isValid()) {
            return Render.view("hotel/all.xhtml", getClass())
                    .param("hotels", hotels)
                    .param("providers", providers)
                    .render();
        } else {
            return Render.view("hotel/none.xhtml", getClass())
                    .param("hotels", hotels)
                    .param("providers", providers)
                    .render();
        }
    }

    @GET
    @Path("index")
    public Response index() {
        return Render.view("index.xhtml", getClass())
                .render();
    }
}
