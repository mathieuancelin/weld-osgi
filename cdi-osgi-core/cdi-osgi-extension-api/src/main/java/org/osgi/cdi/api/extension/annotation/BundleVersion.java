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
 * <p>This annotation qualifies an injection point that represents a bundle or a
 * bundle relative object.</p>
 * <p>It allows to specify the version of the bundle, as a required value.</p>
 * <p>The version actually discriminate the injection point, thus this annotation is
 * for specific bundle relative injection point. For global bundle relative
 * injection point see {@link OSGiBundle} annotation. To discriminate the
 * bundle symbolic name see {@link BundleName}.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Qualifier
 * @see org.osgi.framework.Bundle
 * @see OSGiBundle
 * @see BundleName
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleVersion {

    /**
     * The bundle version. Discriminatory value for the typesafe resolution algorithm.
     *
     * @return the bundle version.
     */
    String value();
}
