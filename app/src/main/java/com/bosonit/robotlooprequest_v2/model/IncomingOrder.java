package com.bosonit.robotlooprequest_v2.model;

import java.util.Map;

public class IncomingOrder {
    String action;
    Map<String, Object> arguments;

    public IncomingOrder(String type, Map<String, Object> arguments) {
        this.action = type;
        this.arguments = arguments;
    }

    public IncomingOrder() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "IncomingOrder{" +
                "action='" + action + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
