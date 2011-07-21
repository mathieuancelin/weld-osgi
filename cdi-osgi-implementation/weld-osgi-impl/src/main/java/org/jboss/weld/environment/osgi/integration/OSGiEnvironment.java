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
package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * The {@link Environment} required for Weld to run in CDI-OSGi.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class OSGiEnvironment implements Environment {

    @Override
    public Set<Class<? extends Service>> getRequiredDeploymentServices() {
        HashSet<Class<? extends Service>> set = new HashSet<Class<? extends Service>>();
        set.add(ScheduledExecutorServiceFactory.class);
        return set;
    }

    @Override
    public Set<Class<? extends Service>> getRequiredBeanDeploymentArchiveServices() {
        HashSet<Class<? extends Service>> set = new HashSet<Class<? extends Service>>();
        set.add(ResourceLoader.class);
        return set;
    }
}