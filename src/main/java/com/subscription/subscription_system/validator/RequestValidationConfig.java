package com.subscription.subscription_system.validator;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RequestValidationConfig {

    RequestValidationItem userSignup;
    RequestValidationItem userCreate;
    RequestValidationItem userLogin;
    RequestValidationItem adminCreate;
    RequestValidationItem userDetails;
    RequestValidationItem userList;
    RequestValidationItem userEdit;
    RequestValidationItem userDelete;
    RequestValidationItem planCreate;
    RequestValidationItem planEdit;
    RequestValidationItem feedbackCreate;
    RequestValidationItem feedbackEdit;
    RequestValidationItem supportTicketRequestCreate;
    RequestValidationItem supportResponseCreate;
    RequestValidationItem supportTicketRequestEdit;
    RequestValidationItem supportResponseEdit;


}
