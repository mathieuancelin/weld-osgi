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
package org.osgi.cdi.impl.extension;

import org.osgi.cdi.api.extension.annotation.Filter;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Helper class representing an instantiable {@link Filter}.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiFilterQualifierType 
        extends AnnotationLiteral<Filter>
        implements Filter {

    private final String value;

    protected OSGiFilterQualifierType(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
