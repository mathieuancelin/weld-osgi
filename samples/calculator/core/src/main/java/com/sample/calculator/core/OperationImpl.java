package com.sample.calculator.core;

import com.sample.calculator.api.Operation;
import com.sample.calculator.api.Operator;

public class OperationImpl implements Operation {

    private Operator operator;

    private int value1;

    private int value2;

    private boolean v1 = false;
    private boolean v2 = false;
    private boolean op = false;


    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public void setOperator(Operator operator) {
        this.op = true;
        this.operator = operator;
    }

    @Override
    public int getValue1() {
        return value1;
    }

    @Override
    public void setValue1(int value1) {
        this.v1 = true;
        this.value1 = value1;
    }

    @Override
    public int getValue2() {
        return value2;
    }

    @Override
    public void setValue2(int value2) {
        this.v2 = true;
        this.value2 = value2;
    }

    @Override
    public int value() {
        return operator.value(value1, value2);
    }

    @Override
    public boolean isValue1Set() {
        return v1;
    }

    @Override
    public boolean isValue2Set() {
        return v2;
    }

    @Override
    public boolean isOperatorSet() {
        return op;
    }
}
