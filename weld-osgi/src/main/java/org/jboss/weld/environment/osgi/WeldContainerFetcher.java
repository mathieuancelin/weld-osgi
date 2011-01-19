package org.jboss.weld.environment.osgi;

import org.osgi.framework.Bundle;

/**
 *
 * @author mathieu
 */
public interface WeldContainerFetcher {

    WeldContainer getContainer(Bundle bundle);
}
