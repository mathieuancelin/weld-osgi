/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sample.osgi.cdi.startable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 *
 * @author mathieu
 */
public interface Starter {

    @PostConstruct
    void init();

    @PreDestroy
    void stop();

}
