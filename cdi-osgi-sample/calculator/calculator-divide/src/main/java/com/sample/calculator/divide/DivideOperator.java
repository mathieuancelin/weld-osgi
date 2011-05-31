package com.sample.calculator.divide;

import com.sample.calculator.api.Operator;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@ApplicationScoped
public class DivideOperator implements Operator {

    @Override
    public int value(int a, int b) {
        return a / b;
    }

    @Override
    public String label() {
        return "/";
    }

}
