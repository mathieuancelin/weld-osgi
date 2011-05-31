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

package org.osgi.cdi.api.extension.events;

/**
 * <p>This class represents all bean bundle invalidation event.</p>
 * <p>It allows to:<ul>
 * <li>
 * <p>Represent all bean bundle invalidation events,</p>
 * </li>
 * <li>
 * <p>Retrieve the invalidated bean bundle and its information.</p>
 * </li>
 * </ul></p>
 * <p>It may be used in <code>Observes</code> method in order to listen all bean
 * bundle invalidation events.</p>
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 * @see org.osgi.framework.Bundle
 * @see org.osgi.cdi.api.extension.BundleState
 * @see Valid
 */
public class Invalid {
    // TODO : find a better name
    // as @UnconsistantDependencies
    // or make it as a start/stop event for container only when deps. are OK
}
