package com.sample.osgi.bundle2.util;

import com.sample.osgi.bundle1.api.Bundle1Listener;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.enterprise.context.ApplicationScoped;

@Publish
@ApplicationScoped
public class Bundle1ListenerImpl implements Bundle1Listener {

    private int stop = 0;

    @Override
    public int getStop() {
        return stop;
    }

    @Override
    public void setStop(int stop) {
        this.stop = stop;
    }
}
