package com.subscription.subscription_system.exception;

public enum ApplicationErrorCode {
    MISSING_MANDATORY_FIELD(new ApplicationError("Missing Mandatory Field : %s")),
    INVALID_INPUT_FORMAT(new ApplicationError("Invalid Input Format : %s")),
    INVALID_FIELD_SIZE(new ApplicationError("Invalid Field Size : %s"));


    private final ApplicationError error;

    ApplicationErrorCode(ApplicationError error) {
        this.error = error;
    }

    public ApplicationError getError() {
        return error;
    }

}
