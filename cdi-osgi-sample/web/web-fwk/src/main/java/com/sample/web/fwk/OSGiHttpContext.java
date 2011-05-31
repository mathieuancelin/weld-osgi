package com.sample.web.fwk;

import java.io.IOException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.http.HttpContext;

public class OSGiHttpContext implements HttpContext {

    private final ClassLoader loader;

    public OSGiHttpContext(ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public boolean handleSecurity(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        return true; // TODO support pluggable security
    }

    @Override
    public URL getResource(String name) {
        return loader.getResource(name.replace("tmp/", ""));
    }

    @Override
    public String getMimeType(String name) {
        return "*"; // TODO map with real types
    }
}
