package com.sample.impl;

import com.sample.api.HelloWorld;
import com.sample.api.Language;
import com.sample.api.Presentation;

@Language("FRENCH")
public class HelloWorldFrench implements HelloWorld {

    @Override @Presentation
    public void sayHello() {
        System.out.println("Bonjour le Monde !");
    }

    @Override
    public void sayGoodbye() {
        System.out.println("Au revoir le Monde !");
    }
}
