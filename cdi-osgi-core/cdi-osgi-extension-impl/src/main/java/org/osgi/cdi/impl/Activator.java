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
package org.osgi.cdi.impl;

import org.osgi.cdi.impl.extension.ExtensionActivator;
import org.osgi.cdi.impl.integration.IntegrationActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This is the {@link BundleActivator} of the extension bundle. It represents the entry point of CDI-OSGi.
 * <p/>
 * It is responsible for starting both extension and integration part of CDI-OSGi. First the extension is started, then
 * the integration.
 * It also stops both part when CDI-OSGi shutdown.
 *
 * @author Guillaume Sauthier
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class Activator implements BundleActivator {

    private BundleActivator integration = new IntegrationActivator();

    private BundleActivator extension = new ExtensionActivator();

    @Override
    public void start(BundleContext context) throws Exception {
        extension.start(context);
        integration.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        integration.stop(context);
        extension.stop(context);
    }
}
