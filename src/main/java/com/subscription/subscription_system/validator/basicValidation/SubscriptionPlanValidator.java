package com.subscription.subscription_system.validator.basicValidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.subscription_system.constants.AppFieldConstants;
import com.subscription.subscription_system.dto.PlanCreateRequestDto;
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
public class SubscriptionPlanValidator {
    private RequestValidationConfig requestValidationConfig;

    private final ApplicationContext applicationContext;

    public SubscriptionPlanValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:support\\json\\subscriptionPlan.json");
        requestValidationConfig = new ObjectMapper().readValue(ResourcesUtils.getResourceContent(resource),
                RequestValidationConfig.class);
    }


    public void validatePlanCreate(String adminId, PlanCreateRequestDto planCreateRequestDto) throws ValidationException {

        // 🧩 Combine header + body params
        Map<String, String> actualParameters = getPlanCreateParams(adminId, planCreateRequestDto);

        // ---------------- HEADER VALIDATION ----------------
        log.trace("Validate mandatory field for Plan Create Header");

        Map<String, String> formatMapDisplayNameHeader = requestValidationConfig.getPlanCreate().getHeader().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getPlanCreate().getHeader().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError validationErrorHeader = CommonRequestValidator.validateMandatoryFields(
                actualParameters,
                mandatoryHeader,
                formatMapDisplayNameHeader
        );

        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for Plan Create Header");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getPlanCreate().getHeader().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorHeader = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Plan Create Header");
        Map<String, String> formatMapHeader = requestValidationConfig.getPlanCreate().getHeader().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorHeader = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }

        // ---------------- BODY VALIDATION ----------------
        log.trace("Validate mandatory field for Plan Create Body");

        Map<String, String> formatMapDisplayNameBody = requestValidationConfig.getPlanCreate().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig.getPlanCreate().getBody().stream()
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

        log.trace("Validate Field size for Plan Create Body");
        Map<String, Integer> fieldSizeMapBody = requestValidationConfig.getPlanCreate().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorBody = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Plan Create Body");
        Map<String, String> formatMapBody = requestValidationConfig.getPlanCreate().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorBody = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }
    }


    private Map<String, String> getPlanCreateParams(String adminId, PlanCreateRequestDto planCreateRequestDto) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.USERID, adminId);
        actualParameters.put(AppFieldConstants.PLAN_NAME, planCreateRequestDto.getPlanName());
        actualParameters.put(AppFieldConstants.PLAN_COST, planCreateRequestDto.getPlanCost());
        actualParameters.put(AppFieldConstants.PLAN_TYPE, planCreateRequestDto.getPlanType());
        return actualParameters;
    }
}
