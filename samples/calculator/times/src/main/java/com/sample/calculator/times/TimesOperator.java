package com.sample.calculator.times;

import com.sample.calculator.api.Operator;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@ApplicationScoped
public class TimesOperator implements Operator {

    @Override
    public int value(int a, int b) {
        return a * b;
    }

    @Override
    public String label() {
        return "*";
    }

}
