package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.SubscriptionPlanService;
import com.subscription.subscription_system.validator.basicValidation.SubscriptionPlanValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/")
public class SubscriptionPlanController {

    @Autowired
    SubscriptionPlanValidator subscriptionPlanValidator;

    @Autowired
    SubscriptionPlanService subscriptionPlanService;

    @PostMapping("/create/plan")
    public ResponseEntity<CommonResponseDto> createPlan(
            @RequestHeader("User-Id") String userId, //Admin Id
            @RequestBody PlanCreateRequestDto planCreateRequestDto) throws CommonException {
        log.info("Basic Validation for Creating subscription plan");
        subscriptionPlanValidator.validatePlanCreate(userId, planCreateRequestDto);

        log.info("Create Subscription Plan ");
        SubscriptionPlanEntity subscriptionPlanEntity = subscriptionPlanService.createPlan(userId, planCreateRequestDto);

        CommonResponseDto response = new CommonResponseDto("Plan created successfully", HttpStatus.CREATED.value(), subscriptionPlanEntity);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/planDetails")
    public ResponseEntity<CommonResponseDto> getPlanDetails(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetPlanId") String planId) throws CommonException {
        log.info("Basic Validation for Getting subscription plan Details");
        if (userId == null || planId == null) {
            throw new CommonException("UserId or PlanId is invalid", HttpStatus.BAD_REQUEST.value());
        }

        PlanDetailsDto planDetailsDto = subscriptionPlanService.getPlanDetails(userId, planId);
        CommonResponseDto response = new CommonResponseDto("Plan details fetched successfully", HttpStatus.OK.value(), planDetailsDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/planList")
    public ResponseEntity<CommonResponseDto> getPlanList(@RequestHeader("User-Id") String userId,
                                                         @RequestParam(required = false) String search,
                                                         @RequestParam(required = false) String filterBy,  // format: key:value,key:value
                                                         @RequestParam(required = false, defaultValue = "firstName") String sortBy,
                                                         @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                         @RequestParam(required = false, defaultValue = "0") int offset,

                                                         @RequestParam(required = false, defaultValue = "10") int limit) throws CommonException {
        log.info("Basic validation for getting Plan List");
        if (userId == null) {
            throw new CommonException("UserId is invalid", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Fetching Plan details list");
        CommonPaginatedResponse<PlanDetailsDto> planList = subscriptionPlanService.getPlanList(userId, search, filterBy, sortBy, sortDir, offset, limit);

        CommonResponseDto response = new CommonResponseDto("Plan List fetched successfully", HttpStatus.OK.value(), planList);

        return ResponseEntity.ok(response);

    }

    @PutMapping("/edit/plan")
    public ResponseEntity<CommonResponseDto> editPlan(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetPlanId") String targetPlanId,
            @RequestBody PlanEditRequestDto editPlanDto) throws CommonException {

        log.info("Validating edit permission for Plan: {}", targetPlanId);
        if (userId == null || targetPlanId == null) {
            throw new CommonException("UserId and Targeted Plan Id are Mandatory", HttpStatus.BAD_REQUEST.value());
        }
        subscriptionPlanValidator.validateEditPlan(editPlanDto);

        log.info("Editing Plan details for ID: {}", editPlanDto);
        PlanDetailsDto updatedPlan = subscriptionPlanService.editPlan(userId, targetPlanId, editPlanDto);

        CommonResponseDto response = new CommonResponseDto(
                "Plan updated successfully", HttpStatus.OK.value(), updatedPlan);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/plan")
    public ResponseEntity<CommonResponseDto> deletePlan(
            @RequestHeader String userId,
            @RequestParam("targetPlanId") String targetPlanId) throws CommonException {

        log.info("Validating Delete permission for Plan: {}", targetPlanId);
        if (userId == null || targetPlanId == null) {
            throw new CommonException("UserId and Targeted Plan Id are Mandatory", HttpStatus.BAD_REQUEST.value());
        }

        subscriptionPlanService.deletePlan(userId, targetPlanId);

        CommonResponseDto response = new CommonResponseDto(
                "Plan deleted successfully", HttpStatus.OK.value(), null);

        return ResponseEntity.ok(response);
    }
}
