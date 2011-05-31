package org.jboss.weld.environment.osgi;

import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.Bundle;

import java.util.*;

/**
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldCDIContainerFactory implements CDIContainerFactory {

    private final Set<String> blackList;
    private Map<Long, CDIContainer> containers = new HashMap<Long, CDIContainer>();

    public WeldCDIContainerFactory() {
        blackList = new HashSet<String>();
        blackList.add("java.io.Serializable");
        blackList.add("org.jboss.interceptor.proxy.LifecycleMixin");
        blackList.add("org.jboss.interceptor.util.proxy.TargetInstanceProxy");
        blackList.add("javassist.util.proxy.ProxyObject");
    }

    @Override
    public CDIContainer createContainer(Bundle bundle) {
        return new WeldCDIContainer(bundle);
    }

    @Override
    public CDIContainer container(Bundle bundle) {
        if(!containers.containsKey(bundle.getBundleId())) {
            return null;
        }
        return containers.get(bundle.getBundleId());
    }

    @Override
    public Collection<CDIContainer> containers() {
        return containers.values();
    }

    @Override
    public void removeContainer(Bundle bundle) {
        containers.remove(bundle.getBundleId());
    }

    @Override
    public void addContainer(CDIContainer container) {
        containers.put(container.getBundle().getBundleId(),container);
    }

    @Override
    public String getID() {
        return Weld.class.getName();
    }

    @Override
    public Set<String> getContractBlacklist() {
        return blackList;
    }

}
