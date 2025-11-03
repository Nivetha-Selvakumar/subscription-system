package com.subscription.subscription_system.validator;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RequestValidationConfig {

    RequestValidationItem userCreate;

}
