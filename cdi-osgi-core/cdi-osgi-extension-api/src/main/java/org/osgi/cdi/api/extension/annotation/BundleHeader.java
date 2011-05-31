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
 * <p>This annotation qualifies an injection point that represents a specific bundle
 * header.</p>
 * <p>It allows to specify the name of the bundle header, as a required
 * value.</p>
 * <p>The header name is <b>not</b> actually qualifying
 * the injection point, thus this annotation is for global specific bundle header
 * injection point with additional data. To discriminate the bundle use
 * {@link OSGiBundle} or {@link BundleName} and
 * {@link BundleVersion} annotations. To obtain all the bundle headers see
 * {@link BundleHeaders} annotations.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Qualifier
 * @see org.osgi.framework.Bundle
 * @see OSGiBundle
 * @see BundleName
 * @see BundleVersion
 * @see BundleHeaders
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleHeader {

    /**
     * The name of the specific bundle header. Nondiscriminatory value for the typesafe resolution algorithm.
     *
     * @return the name of the bundle header.
     */
    @Nonbinding String value();
}
