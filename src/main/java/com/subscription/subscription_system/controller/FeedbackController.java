package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.FeedbackEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.FeedbackService;
import com.subscription.subscription_system.validator.basicValidation.FeedbackValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/")
public class FeedbackController {

    @Autowired
    FeedbackService feedbackService;

    @Autowired
    FeedbackValidator feedbackValidator;

    @PostMapping("/create/feedback")
    public ResponseEntity<CommonResponseDto> createHelpAndFeedback(
            @RequestHeader("User-Id") String userId,
            @RequestBody FeedbackCreateRequestDto feedbackCreateRequest
    ) throws CommonException {
        log.info("Basic Validation for Creating Feedback");
        if (userId == null) {
            throw new CommonException("UserId is invalid", HttpStatus.BAD_REQUEST.value());
        }
        feedbackValidator.validateFeedbackCreate(feedbackCreateRequest);

        log.info("Creating Feedback");
        FeedbackEntity feedbackEntity = feedbackService.createFeedback(userId, feedbackCreateRequest);

        CommonResponseDto response = new CommonResponseDto("Plan created successfully", HttpStatus.CREATED.value(), feedbackEntity);

        log.info("Feedback Created successfully");
        return ResponseEntity.ok(response);

    }

    @GetMapping("/get/feedbackDetails")
    public ResponseEntity<CommonResponseDto> getFeedbackDetails(
            @RequestHeader("User-Id") String userId,
            @RequestParam("feedbackId") String feedbackId) throws CommonException {

        log.info("Basic Validation for Getting Feedback plan Details");
        if (userId == null || feedbackId == null) {
            throw new CommonException("UserId or Feedback Id is invalid", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Getting Feedback plan Details");
        FeedbackDetailsDto feedbackDetailsDto = feedbackService.getFeedbackDetails(userId, feedbackId);

        log.info("Details fetched successfully");
        CommonResponseDto response = new CommonResponseDto("Fetched Feedback Details successfully", HttpStatus.OK.value(), feedbackDetailsDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/feedbackList")
    public ResponseEntity<CommonResponseDto> getFeedbackList(@RequestHeader("User-Id") String userId,
                                                             @RequestParam(required = false) String search,
                                                             @RequestParam(required = false) String filterBy,  // format: key:value,key:value
                                                             @RequestParam(required = false, defaultValue = "firstName") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                             @RequestParam(required = false, defaultValue = "0") int offset,
                                                             @RequestParam(required = false, defaultValue = "10") int limit) throws CommonException {
        log.info("Basic validation for getting Feedback List");
        if (userId == null) {
            throw new CommonException("UserId is invalid", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Fetching Feedback details list");
        CommonPaginatedResponse<FeedbackDetailsDto> feedbackList = feedbackService.getFeedbackList(userId, search, filterBy, sortBy, sortDir, offset, limit);

        CommonResponseDto response = new CommonResponseDto("Feedback List Fetched successfully", HttpStatus.OK.value(), feedbackList);

        log.info("Fetching Feedback details list successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/feedback")
    public ResponseEntity<CommonResponseDto> editFeedback(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetFeedbackId") String targetFeedbackId,
            @RequestBody FeedbackEditRequestDto feedbackEditRequestDto) throws CommonException {

        log.info("Validating edit permission for Feedback: {}", targetFeedbackId);
        if (userId == null || targetFeedbackId == null) {
            throw new CommonException("UserId and Targeted Feedback Id are Mandatory", HttpStatus.BAD_REQUEST.value());
        }
        feedbackValidator.validateEditFeedback(feedbackEditRequestDto);

        log.info("Editing Feedback details for ID: {}", feedbackEditRequestDto);
        FeedbackDetailsDto updatedPlan = feedbackService.editFeedback(feedbackEditRequestDto,userId,targetFeedbackId);

        CommonResponseDto response = new CommonResponseDto(
                "Feedback updated successfully", HttpStatus.OK.value(), updatedPlan);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/feedback")
    public ResponseEntity<CommonResponseDto> deleteFeedback(
            @RequestHeader String userId,
            @RequestParam("targetFeedbackId") String targetFeedbackId) throws CommonException {

        log.info("Validating Delete permission for Feedback: {}", targetFeedbackId);
        if (userId == null || targetFeedbackId == null) {
            throw new CommonException("UserId and Targeted Feedback Id are Mandatory", HttpStatus.BAD_REQUEST.value());
        }

        feedbackService.deleteFeedback(userId, targetFeedbackId);

        CommonResponseDto response = new CommonResponseDto(
                "Feedback deleted successfully", HttpStatus.OK.value(), null);

        return ResponseEntity.ok(response);
    }


}
