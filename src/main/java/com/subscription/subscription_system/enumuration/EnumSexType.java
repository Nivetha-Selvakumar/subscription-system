package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumSexType {

    FEMALE("FEMALE", "Female"),
    MALE("MALE", "Male"),
    OTHER("OTHER", "Other");

    private final String value;
    private final String name;

    EnumSexType(String value, String name) {
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
    public static EnumSexType fromValue(String value) {
        for (EnumSexType e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
