package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.SubscriptionService;
import com.subscription.subscription_system.validator.basicValidation.SubscriptionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/")
public class SubscriptionController {

    @Autowired
    SubscriptionValidator subscriptionValidator;

    @Autowired
    SubscriptionService subscriptionService;

    @PostMapping("/create/subscription")
    public ResponseEntity<CommonResponseDto> createSubscription(
            @RequestHeader("User-Id") String userId,
            @RequestParam("planId") String planId,
            @RequestBody SubscriptionCreateDto subscriptionCreateDto) throws CommonException {
        log.info("Basic Validation for Creating subscription");
        subscriptionValidator.validateSubscriptionCreate(userId, planId, subscriptionCreateDto);

        log.info("Create Subscription for a user");
        SubscriptionDetailsDto subscriptionDetailsDto = subscriptionService.createSubscription(userId, planId, subscriptionCreateDto);
        CommonResponseDto response = new CommonResponseDto(subscriptionDetailsDto.getLastPaymentStatus().equals("FAILED") ?"Subscription Failed!":"Subscription created successfully", HttpStatus.CREATED.value(), subscriptionDetailsDto);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/subscription")
    public ResponseEntity<CommonResponseDto> updateSubscription( //renew subscritpion
                                                                 @RequestHeader("User-Id") String userId,
                                                                 @RequestParam("planId") String planId,
                                                                 @RequestBody SubscriptionEditDto subscriptionEditDto) throws CommonException {
        log.info("Basic Validation for Edit Renew subscription");
        subscriptionValidator.validateSubscriptionEdit(userId, planId, subscriptionEditDto);

        log.info("Renew Subscription for a user");
        SubscriptionDetailsDto subscriptionDetailsDto = subscriptionService.editSubscription(userId, planId, subscriptionEditDto);
        CommonResponseDto response = new CommonResponseDto("Subscription Renew successfully", HttpStatus.OK.value(), subscriptionDetailsDto);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/cancel/subscription")
    public ResponseEntity<CommonResponseDto> cancelSubscription( //renew subscritpion
                                                                 @RequestHeader("User-Id") String userId,
                                                                 @RequestParam("planId") String planId
    ) throws CommonException {
        log.info("Basic Validation for Cancel subscription");
        subscriptionValidator.validateSubscriptionCancel(userId, planId);

        log.info("Cancel Subscription for a user");
        SubscriptionDetailsDto subscriptionDetailsDto = subscriptionService.cancelSubscription(userId, planId);
        CommonResponseDto response = new CommonResponseDto("Subscription Cancelled successfully", HttpStatus.OK.value(), subscriptionDetailsDto);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/get/subscriptionDetails")
    public ResponseEntity<CommonResponseDto> getSubscriptionDetails(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetSubscriptionId") String targetSubscriptionId) throws CommonException {
        log.info("Basic Validation for Getting subscription Details");
        if (userId == null || targetSubscriptionId == null) {
            throw new CommonException("UserId or PlanId is invalid", HttpStatus.BAD_REQUEST.value());
        }

        SubscriptionDetailsDto subscriptionDetailsDto = subscriptionService.getSubscriptionDetails(userId, targetSubscriptionId);
        CommonResponseDto response = new CommonResponseDto("Subscription details fetched successfully", HttpStatus.OK.value(), subscriptionDetailsDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/subscriptionPaymentList")
    public ResponseEntity<CommonResponseDto> getSubscriptionPaymentList(@RequestHeader("User-Id") String userId,
                                                                        @RequestParam(required = false) String search,
                                                                        @RequestParam(required = false) String filterBy,  // format: key:value,key:value
                                                                        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                        @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                                        @RequestParam(required = false, defaultValue = "0") int offset,

                                                                        @RequestParam(required = false, defaultValue = "10") int limit) throws CommonException {
        log.info("Basic validation for getting Subscription List");
        if (userId == null) {
            throw new CommonException("UserId is invalid", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Fetching Subscription details list");
        CommonPaginatedResponse<SubscriptionDetailsDto> subscriptionList = subscriptionService.getSubscriptionPaymentList(userId, search, filterBy, sortBy, sortDir, offset, limit);

        CommonResponseDto response = new CommonResponseDto("Subscription Payment List fetched successfully", HttpStatus.OK.value(), subscriptionList);

        return ResponseEntity.ok(response);

    }

}
