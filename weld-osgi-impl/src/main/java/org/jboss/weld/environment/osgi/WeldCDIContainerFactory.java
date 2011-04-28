package org.jboss.weld.environment.osgi;

import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldCDIContainerFactory implements CDIContainerFactory {

    private final Set<String> blackList;

    public WeldCDIContainerFactory() {
        blackList = new HashSet<String>();
        blackList.add("java.io.Serializable");
        blackList.add("org.jboss.interceptor.proxy.LifecycleMixin");
        blackList.add("org.jboss.interceptor.util.proxy.TargetInstanceProxy");
        blackList.add("javassist.util.proxy.ProxyObject");
    }

    @Override
    public CDIContainer container(Bundle bundle) {
        return new WeldCDIContainer(bundle);
    }

    @Override
    public Class<? extends CDIContainerFactory> delegateClass() {
        return WeldCDIContainerFactory.class;
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
