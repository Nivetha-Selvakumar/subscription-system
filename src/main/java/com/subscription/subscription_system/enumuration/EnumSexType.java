package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumSexType {

    F("F","Female"),
    M("M","Male");

    private final String value;
    private final String name;

    EnumSexType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonValue
    public String getName() {
        return name;
    }


    @JsonCreator
    public static EnumSexType fromValue(String value) {
        for (EnumSexType e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
