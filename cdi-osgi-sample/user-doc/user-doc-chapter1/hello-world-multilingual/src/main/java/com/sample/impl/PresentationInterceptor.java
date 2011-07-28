package com.sample.impl;

import com.sample.api.Presentation;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Presentation
public class PresentationInterceptor {

    @AroundInvoke
    public Object present(InvocationContext ctx) throws Exception {
        ctx.proceed();
        System.out.println("from hello-world-multilingual");
        return null;
    }
}
