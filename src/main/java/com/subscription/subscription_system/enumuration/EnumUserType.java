package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumUserType {

    ADMIN("Admin"),
    SUBSCRIBER("Subscriber");

    private final String value;

    EnumUserType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EnumUserType fromValue(String value) {
        for (EnumUserType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
