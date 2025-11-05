package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumStatusType {

    ACTIVE("ACTIVE", "Active"),
    INACTIVE("INACTIVE", "Inactive");

    private final String value;
    private final String name;

    EnumStatusType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static EnumStatusType fromValue(String value) {
        for (EnumStatusType e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
