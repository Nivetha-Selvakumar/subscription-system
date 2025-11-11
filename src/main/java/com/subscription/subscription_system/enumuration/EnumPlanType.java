package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumPlanType {
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY");

    private final String value;

    EnumPlanType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EnumPlanType fromValue(String value) {
        for (EnumPlanType type : EnumPlanType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Plan Type: " + value);
    }
}
