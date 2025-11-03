package com.subscription.subscription_system.validator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestComponent {

    String name;
    Boolean required;
    Integer maxLength;
    String format;
    String displayName;

    public RequestComponent() {
        super();
    }

}
