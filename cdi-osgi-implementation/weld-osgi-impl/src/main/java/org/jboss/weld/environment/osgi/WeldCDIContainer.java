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
package org.jboss.weld.environment.osgi;

import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.ExtensionActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This is the {@link CDIContainer} implementation using Weld.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class WeldCDIContainer implements CDIContainer {

    private Logger logger = LoggerFactory.getLogger(WeldCDIContainer.class);

    private final Bundle bundle;
    private Weld container;
    private Collection<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();

    public WeldCDIContainer(Bundle bundle) {
        logger.debug("Creation of a new Weld CDI container for bundle {}", bundle);
        this.bundle = bundle;
        container = new Weld(bundle);
    }

    @Override
    public void setRegistrations(Collection<ServiceRegistration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public Collection<ServiceRegistration> getRegistrations() {
        return registrations;
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public boolean shutdown() {
        logger.debug("Weld CDI container is shutting down for bundle {}", bundle);
        return container.shutdown();
    }

    @Override
    public void fire(InterBundleEvent event) {
        logger.debug("Weld CDI container for bundle {} is firing an inter bundle event: {}", bundle, event);
        Long set = CDIOSGiExtension.currentBundle.get();
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        container.getEvent().select(InterBundleEvent.class,
                new ExtensionActivator.SpecificationAnnotation(event.type()),
                new ExtensionActivator.SentAnnotation()).fire(event);
        if (set != null) {
            CDIOSGiExtension.currentBundle.set(set);
        } else {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    @Override
    public boolean initialize() {
        return container.initialize();
    }

    @Override
    public boolean isStarted() {
        return container.isStarted();
    }

    @Override
    public Event getEvent() {
        return container.getInstance().select(Event.class).get();
    }

    @Override
    public BeanManager getBeanManager() {
        return container.getBeanManager();
    }

    @Override
    public Instance<Object> getInstance() {
        return container.getInstance();
    }

    @Override
    public Collection<String> getBeanClasses() {
        return container.getBeanClasses();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeldCDIContainer)) {
            return false;
        }

        WeldCDIContainer that = (WeldCDIContainer) o;

        if (bundle != null ? !bundle.equals(that.bundle) : that.bundle != null) {
            return false;
        }
        if (container != null ? !container.equals(that.container) : that.container != null) {
            return false;
        }
        if (registrations != null ? !registrations.equals(that.registrations) : that.registrations != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = bundle != null ? bundle.hashCode() : 0;
        result = 31 * result + (container != null ? container.hashCode() : 0);
        result = 31 * result + (registrations != null ? registrations.hashCode() : 0);
        return result;
    }
}
