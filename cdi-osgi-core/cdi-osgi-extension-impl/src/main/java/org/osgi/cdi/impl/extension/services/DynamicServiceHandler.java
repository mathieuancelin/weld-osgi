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

import org.osgi.cdi.api.extension.annotation.Filter;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Handler for OSGi dynamic service in use by {@link org.osgi.cdi.impl.extension.OSGiServiceBean}.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class DynamicServiceHandler implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(DynamicServiceHandler.class);

    private final Bundle bundle;
    private final String name;
    private Filter filter;
    private final ServiceTracker tracker;
    private final long timeout;

    public DynamicServiceHandler(Bundle bundle, String name, Filter filter, OSGiService anno) {
		logger.debug("Creation of a new DynamicServiceHandler for bundle {} as a {} with filter {}", new Object[] {bundle, name, filter.value()});        
		this.bundle = bundle;
        this.name = name;
        this.filter = filter;
        timeout = anno.value();
        try {
            if (filter != null && filter.value() != null && filter.value().length() > 0) {
                this.tracker = new ServiceTracker(bundle.getBundleContext(),
                    bundle.getBundleContext().createFilter(
                        "(&(objectClass=" + name + ")" + filter.value() + ")"),
                    null);
            } else {
                this.tracker = new ServiceTracker(bundle.getBundleContext(), name, null);
            }
        } catch (Exception e) {
            logger.error("Unable to create the DynamicServiceHandler.",e);
            throw new RuntimeException(e);
        }
        this.tracker.open();
    }

    public void closeHandler() {
        this.tracker.close();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.trace("Call on the DynamicServiceHandler {} for method {}", this, method);
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        Object instanceToUse = this.tracker.waitForService(timeout);
        try {
            return method.invoke(instanceToUse, args);
        } catch(Throwable t) {
            logger.error("Unable to find a matching service.", t);
            throw new RuntimeException(t);
        } finally {
            CDIOSGiExtension.currentBundle.remove();
        }
    }
}
