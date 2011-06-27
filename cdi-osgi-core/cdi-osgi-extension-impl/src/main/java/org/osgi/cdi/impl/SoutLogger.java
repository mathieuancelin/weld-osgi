package org.osgi.cdi.impl;

public class SoutLogger {

    public void error(String msg, Object object, Object object2) {
        System.out.println(msg + " " + object + " " + object2);
    }

    public void error(String msg, Object object) {
//        System.out.println(msg + " " + object);
    }

    public void warn(String msg, Object object) {
//        System.out.println(msg + " " + object);
    }

    public void info(String msg, Object object) {
//        System.out.println(msg + " " + object);
    }

    public void debug(String msg, Object object) {
//        System.out.println(msg + " " + object);
    }

    public void trace(String msg, Object object) {
//        System.out.println(msg + " " + object);
    }

    public void error(String msg) {
//        System.out.println(msg + " " + object);
    }

    public void warn(String msg) {
//        System.out.println(msg);
    }

    public void info(String msg) {
//        System.out.println(msg);
    }

    public void debug(String msg) {
//        System.out.println(msg);
    }

    public void trace(String msg) {
//        System.out.println(msg);
    }
}
