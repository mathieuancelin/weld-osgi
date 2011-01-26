package org.jboss.weld.environment.osgi.api.integration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface CDIOSGiContainer {

    CDIContainer getContainer();

    boolean initialize();

    boolean isStarted();

    void shutdown();

}
