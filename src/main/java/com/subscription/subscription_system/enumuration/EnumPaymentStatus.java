package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumPaymentStatus {
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String value;

    EnumPaymentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EnumPaymentStatus fromValue(String value) {
        for (EnumPaymentStatus type : EnumPaymentStatus.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Payment Type: " + value);
    }
}
