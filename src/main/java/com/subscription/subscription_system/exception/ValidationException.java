package com.subscription.subscription_system.exception;

public class ValidationException extends CommonException {

  public ValidationException(String message, int httpCode) {
    super(message, httpCode);
  }
}
