/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sample.calculator.api;

import com.sample.calculator.api.Operator;

/**
 *
 * @author mathieu
 */
public interface Operation {

    Operator getOperator();

    int getValue1();

    int getValue2();

    void setOperator(Operator operator);

    void setValue1(int value1);

    void setValue2(int value2);

    int value();

    boolean isOperatorSet();

    boolean isValue1Set();

    boolean isValue2Set();

}
