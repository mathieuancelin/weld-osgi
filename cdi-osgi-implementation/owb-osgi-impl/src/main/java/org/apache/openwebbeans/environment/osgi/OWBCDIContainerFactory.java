package org.apache.openwebbeans.environment.osgi;

import org.apache.openwebbeans.environment.osgi.integration.OWB;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OWBCDIContainerFactory implements CDIContainerFactory {

    private final Set<String> blackList;

    public OWBCDIContainerFactory() {
        blackList = new HashSet<String>();
        blackList.add("java.io.Serializable");
        blackList.add("javassist.util.proxy.ProxyObject");
    }

    @Override
    public CDIContainer container(Bundle bundle) {
        return new OWBCDIContainer(bundle);
    }

    @Override
    public CDIContainer containers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getID() {
        return OWB.class.getName();
    }

    @Override
    public Set<String> getContractBlacklist() {
        return blackList;
    }
}
