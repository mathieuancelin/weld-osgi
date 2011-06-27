package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.ContextualService;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.enterprise.context.RequestScoped;

@Publish
@RequestScoped
public class RequestScopedContextualService implements ContextualService {

    private final long id = System.nanoTime();

    @Override
    public long getId() {
        return id;
    }
}