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


import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>This annotation qualifies an injection point that represents a bundle or a bundle relative object.</p>
 * <p>It allows to specify:
 * <ul> <li> <p>The symbolic name of the bundle, as a required value,</p> </li>
 * <li> <p>The version of the bundle, as an optional value.</p> </li> </ul>
 * </p> <p>The symbolic name and version are <b>not</b> actually qualifying the injection point,
 * thus this annotation is for global bundle injection point with additional data.
 * In order to actually discriminate on the symbolic name or version see {@link BundleName} and {@link BundleVersion} annotations.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Qualifier
 * @see org.osgi.framework.Bundle
 * @see BundleName
 * @see BundleVersion
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface OSGiBundle {

    /**
     * The symbolic name of the bundle. Nondiscriminatory value for the typesafe resolution algorithm.
     *
     * @return the symbolic name of the bundle.
     */
    @Nonbinding String value();

    /**
     * The version of the bundle. Nondiscriminatory value for the typesafe resolution algorithm.
     *
     * @return the version of the bundle.
     */
    @Nonbinding String version() default "";
}
