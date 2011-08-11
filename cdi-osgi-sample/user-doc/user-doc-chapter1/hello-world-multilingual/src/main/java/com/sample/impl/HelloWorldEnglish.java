package com.sample.impl;

import com.sample.api.HelloWorld;
import com.sample.api.Language;
import com.sample.api.Presentation;

@Language("ENGLISH")
public class HelloWorldEnglish implements HelloWorld {

    @Override @Presentation
    public void sayHello() {
        System.out.println("Hello World!");
    }

    @Override
    public void sayGoodbye() {
        System.out.println("Goodbye World!");
    }
}
