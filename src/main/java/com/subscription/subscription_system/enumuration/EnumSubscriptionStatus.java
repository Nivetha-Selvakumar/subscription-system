package com.subscription.subscription_system.enumuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumSubscriptionStatus {
    ACTIVE("ACTIVE", "Active"),
    EXPIRED("EXPIRED","EXPIRED"),
    PENDING("PENDING","PENDING"),
    CANCELLED("CANCELLED","CANCELLED");
    private final String value;
    private final String name;

    EnumSubscriptionStatus(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static EnumSubscriptionStatus fromValue(String value) {
        for (EnumSubscriptionStatus e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
