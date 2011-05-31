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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * <p>This annotation notices that this type is an OSGi service implementation and
 * should be automatically published in the OSGi service registry.</p>
 * <p>It allows to specify:<ul>
 * <li>
 * <p>The contract interfaces of implemented service, as an optional
 * array of {@link Class}es,</p>
 * </li>
 * <li>
 * <p>The properties of the published service implementation, as an
 * optional array of {@link Property},</p>
 * </li>
 * </ul></p>
 * <p>The published implementation might be discriminated using regular
 * {@link javax.inject.Qualifier} annotations or a LDAP filter with {@link Filter}
 * annotation.</p>
 * <p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see javax.inject.Qualifier
 * @see Filter
 * @see Property
 * @see org.osgi.cdi.api.extension.Service
 * @see org.osgi.cdi.api.extension.ServiceRegistry
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish {

    /**
     * The contracts the annotated class fulfills.
     *
     * @return the contracts of the annotated implementation as an array of interfaces.
     */
    public Class[] contracts() default {};

    /**
     * The properties of the annotated class as OSGi service properties (for LDAP filtering).
     *
     * @return the properties of the service implementation as an array of {@link Property}.
     */
    public Property[] properties() default {};

}
