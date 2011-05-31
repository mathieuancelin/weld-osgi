package com.sample.calculator.api;

public class NotificationEvent {

    private final String message;

    public NotificationEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
