package com.sample.calculator.api;

public class EqualsEvent {

    private final Operation operation;


    public EqualsEvent(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public int getValue() {
        return operation.value();
    }
}
