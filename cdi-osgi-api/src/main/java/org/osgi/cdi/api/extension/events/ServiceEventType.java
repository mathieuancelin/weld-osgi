package org.osgi.cdi.api.extension.events;

/**
 * <p>Represents all possible bundle state for a bundle event.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see AbstractServiceEvent
 */
public enum ServiceEventType {

    SERVICE_ARRIVAL,
    SERVICE_DEPARTURE,
    SERVICE_CHANGED
}
