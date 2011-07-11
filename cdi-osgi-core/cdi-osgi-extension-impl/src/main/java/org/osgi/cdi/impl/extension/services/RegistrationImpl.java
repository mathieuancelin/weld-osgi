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
package org.osgi.cdi.impl.extension.services;

import org.osgi.cdi.api.extension.Registration;
import org.osgi.cdi.api.extension.RegistrationHolder;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.impl.extension.FilterGenerator;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of {@link Registration}.
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 * @author Matthieu CLOCHARD - SERLI (matthieu.clochard@serli.com)
 */
public class RegistrationImpl<T> implements Registration<T> {

    private static Logger logger = LoggerFactory.getLogger(RegistrationImpl.class);

    private final Class<T> contract;
    private final BundleContext registry;
    private final Bundle bundle;
    private final RegistrationHolder holder;
    private List<Registration<T>> registrations = new ArrayList<Registration<T>>();

    public RegistrationImpl(Class<T> contract,
            BundleContext registry, Bundle bundle,
            RegistrationHolder holder) {
        this.contract = contract;
        this.registry = registry;
        this.holder = holder;
        this.bundle = bundle;
    }

    @Override
    public void unregister() {
        for(ServiceRegistration reg : holder.getRegistrations()) {
            holder.removeRegistration(reg);
            reg.unregister();
        }
    }

    @Override
    public <T> Service<T> getServiceReference() {
        return new ServiceImpl<T>(contract, registry);
    }

    @Override
    public Registration<T> select(Annotation... qualifiers) {
        if (qualifiers == null) {
            throw new IllegalArgumentException("You can't pass null array of qualifiers");
        }
        String filter = FilterGenerator.makeFilter(Arrays.asList(qualifiers)).value();
        return null;
    }

    @Override
    public Registration<T> select(String filter) {
        Filter osgiFilter = null;
        try {
            osgiFilter = FrameworkUtil.createFilter(filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid LDAP filter : " + e.getMessage());
        }
        RegistrationHolder holder = new RegistrationsHolderImpl();
        for(ServiceRegistration registration : holder.getRegistrations()) {
            if(osgiFilter.match(registration.getReference())) {
                holder.addRegistration(registration);
            }
        }
        return new RegistrationImpl<T>(contract,registry,bundle,holder);
    }

    @Override
    public int size() {
        return holder.size();
    }

    @Override
    public Iterator<Registration<T>> iterator() {
        populate();
        return registrations.iterator();
    }

    private void populate() {
        registrations.clear();
        try {
            List<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                registrations.add(new RegistrationImpl<T>(contract, registry, bundle, holder));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
