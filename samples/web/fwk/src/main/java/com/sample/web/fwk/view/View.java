package com.sample.web.fwk.view;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author Mathieu ANCELIN
 */
public class View extends Renderable {

    private static final TemplateRenderer renderer = new TemplateRenderer();
    private static final String TYPE = MediaType.TEXT_HTML;
    private final String viewName;
    private final Map<String, Object> context;
    private final ClassLoader loader;

    public View(String viewName, Class<?> from) {
        this.contentType = TYPE;
        this.viewName = viewName;
        this.context = new HashMap<String, Object>();
        this.loader = from.getClassLoader();
    }

    public View(String viewName, Map<String, Object> context, Class<?> from) {
        this.contentType = TYPE;
        this.viewName = viewName;
        this.context = context;
        this.loader = from.getClassLoader();
    }

    public View param(String name, Object value) {
        this.context.put(name, value);
        return this;
    }

    @Override
    public Response render() {
        try {
            String renderText = renderer.render("views/" + viewName, context, loader);
            ResponseBuilder builder = Response.ok(renderText, TYPE);
            return builder.build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void render(ServletResponse resp) {
        try {
            resp.setContentType("text/html");
            PrintWriter w = resp.getWriter();
            w.println(render());
            w.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
