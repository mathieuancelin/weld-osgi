package com.sample.osgi.bundle1.impl;

import org.osgi.cdi.api.extension.annotation.Publish;

import java.io.Serializable;

@Publish(contracts = {
        Serializable.class
})
public class UnblackListedServiceImpl implements Serializable{
}
