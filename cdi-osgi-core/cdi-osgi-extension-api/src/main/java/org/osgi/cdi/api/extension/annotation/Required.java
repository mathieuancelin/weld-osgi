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

package org.osgi.cdi.api.extension.annotation;


import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>This annotation qualifies an injection point that represents a required
 * service.</p>
 * <p>It may be coupled with the {@link OSGiService} annotation in order to
 * qualify a service that must be available for the application to start.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Qualifier
 * @see OSGiService
 * @see org.osgi.cdi.api.extension.Service
 * @see org.osgi.cdi.api.extension.ServiceRegistry
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
}
