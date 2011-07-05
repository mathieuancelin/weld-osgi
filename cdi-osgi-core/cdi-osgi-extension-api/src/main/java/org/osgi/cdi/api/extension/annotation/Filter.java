/**
 *  Copyright 2010-211
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.osgi.cdi.api.extension.annotation;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * <p>This annotation qualifies an injection point that represents a LDAP filtered
 * service.</p>
 * <p>It allows to specify the LDAP filter, as a required
 * {@link String}.</p>
 * <p>It may be coupled with a {@link OSGiService} or a {@link Publish}
 * annotation in order to filter the injected or published service
 * implementations. The LDAP filtering acts on
 * {@link Qualifier} or {@link Property} annotations or regular OSGi
 * properties used in service publishing.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Qualifier
 * @see Property
 * @see OSGiService
 * @see Publish
 * @see org.osgi.cdi.api.extension.Service
 * @see org.osgi.cdi.api.extension.ServiceRegistry
 */
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface Filter {

    /**
     * The LDAP filter.
     *
     * @return the LDAP filter as a String.
     */
    String value();
}
