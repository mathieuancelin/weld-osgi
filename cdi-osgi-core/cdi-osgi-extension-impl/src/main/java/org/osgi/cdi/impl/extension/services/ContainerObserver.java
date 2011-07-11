/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Collection;

/**
 * Store the CDI container of the current bundle and the all other CDI containers.
 * Broadcast {@link InterBundleEvent} from current bundle.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
@ApplicationScoped
public class ContainerObserver {

    private static Logger logger = LoggerFactory.getLogger(ContainerObserver.class);

    private CDIContainer currentContainer;
    private Collection<CDIContainer> containers;

    public void setCurrentContainer(CDIContainer currentContainer) {
        this.currentContainer = currentContainer;
    }

    public void setContainers(Collection<CDIContainer> containers) {
        this.containers = containers;
    }

    public void listenInterBundleEvents(@Observes InterBundleEvent event) {
        logger.trace("Receiving an inter bundle event");
        if (!event.isSent()) {
            logger.debug("Broadcasting the inter bundle event: {}; from bundle {}", event, currentContainer.getBundle());
            event.sent();
            for (CDIContainer container : containers) {
                if (!container.equals(currentContainer)) {
                    try {
                        logger.trace("Broadcasting the inter bundle event: {}; to bundle {}", event, container.getBundle());
                        container.fire(event);
                    } catch (Throwable t) {
                        logger.warn("InterBundle event broadcast failed", t);
                    }
                }
            }
        }
    }
}
