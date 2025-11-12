package com.subscription.subscription_system.validator.basicValidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.subscription_system.constants.AppFieldConstants;
import com.subscription.subscription_system.dto.FeedbackCreateRequestDto;
import com.subscription.subscription_system.dto.FeedbackEditRequestDto;
import com.subscription.subscription_system.exception.ApplicationErrorCode;
import com.subscription.subscription_system.exception.ErrorMessages;
import com.subscription.subscription_system.exception.ValidationException;
import com.subscription.subscription_system.utils.ResourcesUtils;
import com.subscription.subscription_system.validator.CommonRequestValidator;
import com.subscription.subscription_system.validator.RequestComponent;
import com.subscription.subscription_system.validator.RequestValidationConfig;
import com.subscription.subscription_system.validator.ValidationError;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FeedbackValidator {

    private RequestValidationConfig requestValidationConfig;

    private final ApplicationContext applicationContext;

    public FeedbackValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:support\\json\\feedback.json");
        requestValidationConfig = new ObjectMapper().readValue(ResourcesUtils.getResourceContent(resource),
                RequestValidationConfig.class);
    }


    public void validateFeedbackCreate(FeedbackCreateRequestDto helpAndFeedbackCreateRequest) throws ValidationException {
        Map<String, String> actualParameters = getFeedbackCreateParams(helpAndFeedbackCreateRequest);
        // ---------------- BODY VALIDATION ----------------
        log.trace("Validate mandatory field for Feedback Body");

        Map<String, String> formatMapDisplayNameBody = requestValidationConfig.getFeedbackCreate().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig.getFeedbackCreate().getBody().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError validationErrorBody = CommonRequestValidator.validateMandatoryFields(
                actualParameters,
                mandatoryBody,
                formatMapDisplayNameBody
        );

        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for Feedback Body");
        Map<String, Integer> fieldSizeMapBody = requestValidationConfig.getFeedbackCreate().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorBody = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Feedback Body");
        Map<String, String> formatMapBody = requestValidationConfig.getFeedbackCreate().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorBody = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }
    }

    private Map<String, String> getFeedbackCreateParams(FeedbackCreateRequestDto feedbackCreateRequest) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.RATINGS, feedbackCreateRequest.getRatings());
        actualParameters.put(AppFieldConstants.COMMENTS, feedbackCreateRequest.getComments());

        return actualParameters;
    }

    private Map<String, String> getFeedbackEditParams(FeedbackEditRequestDto feedbackEditRequestDto) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.RATINGS, feedbackEditRequestDto.getRatings());
        actualParameters.put(AppFieldConstants.COMMENTS, feedbackEditRequestDto.getComments());
        actualParameters.put(AppFieldConstants.STATUS, feedbackEditRequestDto.getStatus());
        return actualParameters;
    }


    public void validateEditFeedback(FeedbackEditRequestDto feedbackEditRequestDto) throws ValidationException {
        Map<String, String> actualParameters = getFeedbackEditParams(feedbackEditRequestDto);
        // ---------------- BODY VALIDATION ----------------
        log.trace("Validate mandatory field for Feedback Edit Body");

        Map<String, String> formatMapDisplayNameBody = requestValidationConfig.getFeedbackEdit().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig.getFeedbackEdit().getBody().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError validationErrorBody = CommonRequestValidator.validateMandatoryFields(
                actualParameters,
                mandatoryBody,
                formatMapDisplayNameBody
        );

        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for Feedback Edit Body");
        Map<String, Integer> fieldSizeMapBody = requestValidationConfig.getFeedbackEdit().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorBody = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Feedback Edit Body");
        Map<String, String> formatMapBody = requestValidationConfig.getFeedbackEdit().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorBody = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }
    }
}
