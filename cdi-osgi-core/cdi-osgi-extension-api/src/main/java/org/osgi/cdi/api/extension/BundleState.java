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

package org.osgi.cdi.api.extension;

/**
 * <p>This enumeration lists the two new states of a bean bundle.</p>
 * <p>A bean bundle is in {@link BundleState#VALID} state if all its required service
 * dependencies are validated otherwise is in {@link BundleState#INVALID} state. Every time
 * a bean bundle goes from one state to another a corresponding {@link org.osgi.cdi.api.extension.events.Valid}
 * or {@link org.osgi.cdi.api.extension.events.Invalid} event may be fired.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see org.osgi.framework.Bundle
 * @see org.osgi.cdi.api.extension.events.Valid
 * @see org.osgi.cdi.api.extension.events.Invalid
 */
public enum BundleState {
    VALID, INVALID
}
