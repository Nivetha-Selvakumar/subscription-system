package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumTicketStatus {
    EXPIRED("Expired"),
    OPEN("Open"),
    IN_PROGRESS("In-Progress"),
    CLOSED("Closed");

    private final String value;

    EnumTicketStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EnumTicketStatus fromValue(String value) {
        for (EnumTicketStatus type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
