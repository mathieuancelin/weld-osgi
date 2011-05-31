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
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

/**
 * <p>This annotation qualifies an injection point that represents an
 * {@link org.osgi.cdi.api.extension.events.InterBundleEvent} from outside the current {@link org.osgi.framework.Bundle}.</p>
 * <p>It may be used in an {@link javax.enterprise.event.Observes} method to restrict the listened
 * {@link org.osgi.cdi.api.extension.events.InterBundleEvent}. It allows to ignore the
 * {@link org.osgi.cdi.api.extension.events.InterBundleEvent} from within the current bundle.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see Qualifier
 * @see org.osgi.cdi.api.extension.events.InterBundleEvent
 * @see org.osgi.framework.Bundle
 */
@Qualifier
@Target({PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sent {
}
