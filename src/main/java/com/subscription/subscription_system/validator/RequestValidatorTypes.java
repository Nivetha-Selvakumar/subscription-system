package com.subscription.subscription_system.validator;

public enum RequestValidatorTypes {

    MANDATORY("MANDATORY", "Missing required param"),
    SIZE("SIZE", "Invalid size"),
    FORMAT("FORMAT", "Invalid format"),
    STATUS("STATUS", "Invalid status");

    String type;
    String errorMessage;

    RequestValidatorTypes(String type, String errorMessage) {
        this.type = type;
        this.errorMessage = errorMessage;
    }

    public String getType() {
        return type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
