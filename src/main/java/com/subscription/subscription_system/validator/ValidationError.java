package com.subscription.subscription_system.validator;

public class ValidationError {

    String validationErrorType;
    String field;
    String message;

    public ValidationError() {
        super();
    }

    public ValidationError(String validationErrorType, String field, String message) {
        super();
        this.validationErrorType = validationErrorType;
        this.field = field;
        this.message = message;
    }

    public String getValidationErrorType() {
        return validationErrorType;
    }

    public void setValidationErrorType(String validationErrorType) {
        this.validationErrorType = validationErrorType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
