package com.sample.impl;

import com.sample.api.HelloWorld;
import com.sample.api.Language;
import com.sample.api.Presentation;

@Language("GERMAN")
public class HelloWorldGerman implements HelloWorld {
    
    @Override @Presentation
    public void sayHello() {
        System.out.println("Hallo Welt!");
    }

    @Override
    public void sayGoodbye() {
        System.out.println("Auf Wiedersehen Welt!");
    }
}
