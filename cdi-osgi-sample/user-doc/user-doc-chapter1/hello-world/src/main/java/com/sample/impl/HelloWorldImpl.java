package com.sample.impl;

import com.sample.api.HelloWorld;

public class HelloWorldImpl implements HelloWorld {

    @Override
    public void sayHello() {
        System.out.println("Hello World!");
    }

    @Override
    public void sayGoodbye() {
        System.out.println("Goodbye World!");
    }
}
