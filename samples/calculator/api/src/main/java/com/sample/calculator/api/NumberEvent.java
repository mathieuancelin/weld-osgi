package com.sample.calculator.api;

public class NumberEvent {

    private final int value;

    public NumberEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
