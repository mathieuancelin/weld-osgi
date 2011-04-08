/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.weld.environment.osgi.extension.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jboss.weld.environment.osgi.api.extension.Registration;
import org.jboss.weld.environment.osgi.api.extension.Registrations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class RegistrationsImpl<T> implements Registrations<T> {

    private Class<T> type;

    private List<Registration<T>> registrations = new ArrayList<Registration<T>>();

    private BundleContext registry;

    private RegistrationsHolder holder;

    @Override
    public Iterator<Registration<T>> iterator() {
        populate();
        return registrations.iterator();
    }

    @Override
    public int size() {
        List<ServiceRegistration> regs = holder.getRegistrations();
        return regs.size();
    }

    private void populate() {
        registrations.clear();
        try {
            List<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                registrations.add(new RegistrationImpl<T>(type, reg, registry, holder));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public void setHolder(RegistrationsHolder holder) {
        this.holder = holder;
    }

    public void setRegistry(BundleContext registry) {
        this.registry = registry;
    }
}
