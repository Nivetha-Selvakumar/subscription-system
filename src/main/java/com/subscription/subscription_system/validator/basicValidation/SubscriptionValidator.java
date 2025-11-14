//package com.subscription.subscription_system.validator.basicValidation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.subscription.subscription_system.utils.ResourcesUtils;
//import com.subscription.subscription_system.validator.RequestValidationConfig;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class SubscriptionValidator {
//
//    private RequestValidationConfig requestValidationConfig;
//
//    private final ApplicationContext applicationContext;
//
//    public SubscriptionValidator(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
//
//    @PostConstruct
//    public void init() throws IOException {
//        Resource resource = applicationContext.getResource("classpath:support\\json\\subscription.json");
//        requestValidationConfig = new ObjectMapper().readValue(ResourcesUtils.getResourceContent(resource),
//                RequestValidationConfig.class);
//    }
//
//
//
//}
