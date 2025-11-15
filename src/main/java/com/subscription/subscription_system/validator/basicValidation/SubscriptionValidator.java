package com.subscription.subscription_system.validator.basicValidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.subscription_system.constants.AppFieldConstants;
import com.subscription.subscription_system.dto.SubscriptionCreateDto;
import com.subscription.subscription_system.dto.SubscriptionEditDto;
import com.subscription.subscription_system.exception.ApplicationErrorCode;
import com.subscription.subscription_system.exception.CommonException;
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
public class SubscriptionValidator {

    private RequestValidationConfig requestValidationConfig;

    private final ApplicationContext applicationContext;

    public SubscriptionValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:support\\json\\subscription.json");
        requestValidationConfig = new ObjectMapper().readValue(ResourcesUtils.getResourceContent(resource),
                RequestValidationConfig.class);
    }

    // ---------------------- VALIDATE CREATE ----------------------
    public void validateSubscriptionCreate(
            String userId,
            String planId,
            SubscriptionCreateDto dto
    ) throws CommonException {

        Map<String, String> actualParams = getSubscriptionCreateParams(userId, planId, dto);

        // ========== HEADER ==========
        log.trace("Validate mandatory field for Subscription Create Header");

        Map<String, String> headerDisplayNames = requestValidationConfig
                .getSubscriptionCreate()
                .getHeader()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig
                .getSubscriptionCreate()
                .getHeader()
                .stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError headerError = CommonRequestValidator.validateMandatoryFields(
                actualParams,
                mandatoryHeader,
                headerDisplayNames
        );
        if (headerError != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(headerError.getField(), HttpStatus.BAD_REQUEST.value());
        }

        // Size
        Map<String, Integer> headerSize = requestValidationConfig
                .getSubscriptionCreate()
                .getHeader()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        headerError = CommonRequestValidator.validateFieldSize(actualParams, headerSize, headerDisplayNames);
        if (headerError != null) {
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(headerError.getField(), HttpStatus.BAD_REQUEST.value());
        }

        // Format
        Map<String, String> headerFormat = requestValidationConfig
                .getSubscriptionCreate()
                .getHeader()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        headerError = CommonRequestValidator.validateFieldValueFormat(actualParams, headerFormat, headerDisplayNames);
        if (headerError != null) {
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(headerError.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ========== QUERY PARAM ==========
        log.trace("Validate mandatory field for Subscription Create Query Params");

        Map<String, String> queryDisplayNames = requestValidationConfig
                .getSubscriptionCreate()
                .getQueryParam()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryQuery = requestValidationConfig
                .getSubscriptionCreate()
                .getQueryParam()
                .stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError queryError = CommonRequestValidator.validateMandatoryFields(
                actualParams,
                mandatoryQuery,
                queryDisplayNames
        );
        if (queryError != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(queryError.getField(), HttpStatus.BAD_REQUEST.value());
        }

        Map<String, Integer> querySize = requestValidationConfig
                .getSubscriptionCreate()
                .getQueryParam()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        queryError = CommonRequestValidator.validateFieldSize(actualParams, querySize, queryDisplayNames);
        if (queryError != null) {
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(queryError.getField(), HttpStatus.BAD_REQUEST.value());
        }

        Map<String, String> queryFormat = requestValidationConfig
                .getSubscriptionCreate()
                .getQueryParam()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        queryError = CommonRequestValidator.validateFieldValueFormat(actualParams, queryFormat, queryDisplayNames);
        if (queryError != null) {
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(queryError.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ========== BODY ==========
        log.trace("Validate mandatory field for Subscription Create Body");

        Map<String, String> bodyDisplayNames = requestValidationConfig
                .getSubscriptionCreate()
                .getBody()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig
                .getSubscriptionCreate()
                .getBody()
                .stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError bodyError = CommonRequestValidator.validateMandatoryFields(
                actualParams,
                mandatoryBody,
                bodyDisplayNames
        );
        if (bodyError != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(bodyError.getField(), HttpStatus.BAD_REQUEST.value());
        }

        Map<String, Integer> bodySize = requestValidationConfig
                .getSubscriptionCreate()
                .getBody()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        bodyError = CommonRequestValidator.validateFieldSize(actualParams, bodySize, bodyDisplayNames);
        if (bodyError != null) {
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(bodyError.getField(), HttpStatus.BAD_REQUEST.value());
        }

        Map<String, String> bodyFormats = requestValidationConfig
                .getSubscriptionCreate()
                .getBody()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        bodyError = CommonRequestValidator.validateFieldValueFormat(actualParams, bodyFormats, bodyDisplayNames);
        if (bodyError != null) {
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(bodyError.getField(), HttpStatus.BAD_REQUEST.value());
        }

    }

    // Prepare Params
    private Map<String, String> getSubscriptionCreateParams(String userId, String planId, SubscriptionCreateDto dto) {
        Map<String, String> map = new HashMap<>();
        map.put(AppFieldConstants.USERID, userId);
        map.put(AppFieldConstants.PLAN_ID, planId);
        map.put(AppFieldConstants.AMOUNT, dto.getAmount());
        map.put(AppFieldConstants.PAYMENT_STATUS, dto.getPaymentStatus());
        map.put(AppFieldConstants.CURRENT_SUB_STATUS, dto.getCurrentSubStatus());
        return map;
    }


    // ---------------------------------------------------------------
//  VALIDATE EDIT
// ---------------------------------------------------------------
    public void validateSubscriptionEdit(String userId, String planId, SubscriptionEditDto dto) throws CommonException {

        Map<String, String> actual = new HashMap<>();
        actual.put(AppFieldConstants.USERID, userId);
        actual.put(AppFieldConstants.PLAN_ID, planId);
        actual.put(AppFieldConstants.RENEW_AMOUNT, dto.getRenewAmount().toString());
        actual.put(AppFieldConstants.PAYMENT_STATUS, dto.getPaymentStatus());

        // ---------------- HEADER ----------------
        Map<String, String> headerDisplay = requestValidationConfig.getSubscriptionEdit().getHeader()
                .stream().collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getSubscriptionEdit().getHeader().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError headerErr = CommonRequestValidator.validateMandatoryFields(actual, mandatoryHeader, headerDisplay);
        if (headerErr != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(headerErr.getField(), HttpStatus.BAD_REQUEST.value());
        }

        Map<String, Integer> headerSize = requestValidationConfig.getSubscriptionEdit().getHeader().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        headerErr = CommonRequestValidator.validateFieldSize(actual, headerSize, headerDisplay);
        if (headerErr != null) {
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(headerErr.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ---------------- QUERY PARAM ----------------
        Map<String, String> queryDisplay = requestValidationConfig.getSubscriptionEdit().getQueryParam()
                .stream().collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryQuery = requestValidationConfig.getSubscriptionEdit().getQueryParam().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError queryErr = CommonRequestValidator.validateMandatoryFields(actual, mandatoryQuery, queryDisplay);
        if (queryErr != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(queryErr.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ---------------- BODY ----------------
        Map<String, String> bodyDisplay = requestValidationConfig.getSubscriptionEdit().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig.getSubscriptionEdit().getBody().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError bodyErr = CommonRequestValidator.validateMandatoryFields(actual, mandatoryBody, bodyDisplay);
        if (bodyErr != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(bodyErr.getField(), HttpStatus.BAD_REQUEST.value());
        }

        Map<String, Integer> bodySize = requestValidationConfig.getSubscriptionEdit().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        bodyErr = CommonRequestValidator.validateFieldSize(actual, bodySize, bodyDisplay);
        if (bodyErr != null) {
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(bodyErr.getField(), HttpStatus.BAD_REQUEST.value());
        }

        Map<String, String> bodyFormat = requestValidationConfig.getSubscriptionEdit().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        bodyErr = CommonRequestValidator.validateFieldValueFormat(actual, bodyFormat, bodyDisplay);
        if (bodyErr != null) {
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(bodyErr.getField(), HttpStatus.BAD_REQUEST.value());
        }
    }


    // ---------------------------------------------------------------
//  VALIDATE CANCEL
// ---------------------------------------------------------------
    public void validateSubscriptionCancel(String userId, String planId) throws CommonException {

        Map<String, String> actual = new HashMap<>();
        actual.put(AppFieldConstants.USERID, userId);
        actual.put(AppFieldConstants.PLAN_ID, planId);

        // ------------ HEADER ------------
        Map<String, String> headerDisplay = requestValidationConfig.getSubscriptionCancel().getHeader().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getSubscriptionCancel().getHeader().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError headerErr = CommonRequestValidator.validateMandatoryFields(actual, mandatoryHeader, headerDisplay);
        if (headerErr != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(headerErr.getField(), HttpStatus.BAD_REQUEST.value());
        }

        // ------------ QUERY PARAM ------------
        Map<String, String> queryDisplay = requestValidationConfig.getSubscriptionCancel().getQueryParam().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryQuery = requestValidationConfig.getSubscriptionCancel().getQueryParam().stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError queryErr = CommonRequestValidator.validateMandatoryFields(actual, mandatoryQuery, queryDisplay);
        if (queryErr != null) {
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(queryErr.getField(), HttpStatus.BAD_REQUEST.value());
        }
    }

}
