package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.ContractInterface;
import com.sample.osgi.bundle1.api.NotContractInterface;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish(contracts = {
        ContractInterface.class
})
public class ContractPublishedServiceImpl implements NotContractInterface {
}
