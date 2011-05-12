package org.osgi.cdi.api.extension;

/**
 * <p>Represents the state of bean bundle</p>
 * <p>A bean bundle is in VALID state if all its required service dependencies 
 * are validated otherwise is in INVALID state</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public enum BundleState {
    VALID, INVALID
}
