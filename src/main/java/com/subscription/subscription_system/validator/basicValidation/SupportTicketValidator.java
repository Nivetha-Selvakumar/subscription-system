package com.subscription.subscription_system.validator.basicValidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.subscription_system.constants.AppFieldConstants;
import com.subscription.subscription_system.dto.SupportTicketRequestEditDto;
import com.subscription.subscription_system.dto.SupportTicketResponseCreateDto;
import com.subscription.subscription_system.dto.SupportTicketResponseEditDto;
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
public class SupportTicketValidator {
    private RequestValidationConfig requestValidationConfig;

    private final ApplicationContext applicationContext;

    public SupportTicketValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:support\\json\\supportResponse.json");
        requestValidationConfig = new ObjectMapper().readValue(ResourcesUtils.getResourceContent(resource),
                RequestValidationConfig.class);
    }

    public void validateSupportTicketResponseCreate(String userId, String targetTicketId, SupportTicketResponseCreateDto supportTicketRequestDto) throws ValidationException {
        // 🧩 Combine header + body params
        Map<String, String> actualParameters = getSupportTicketResponseParams(userId, targetTicketId, supportTicketRequestDto);

        // ---------------- HEADER VALIDATION ----------------
        log.trace("Validate mandatory field for Plan Create Header");

        Map<String, String> formatMapDisplayNameHeader = requestValidationConfig.getSupportResponseCreate().getHeader().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getSupportResponseCreate().getHeader().stream()
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
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getSupportResponseCreate().getHeader().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorHeader = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Plan Create Header");
        Map<String, String> formatMapHeader = requestValidationConfig.getSupportResponseCreate().getHeader().stream()
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

        Map<String, String> formatMapDisplayNameBody = requestValidationConfig.getSupportResponseCreate().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig.getSupportResponseCreate().getBody().stream()
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
        Map<String, Integer> fieldSizeMapBody = requestValidationConfig.getSupportResponseCreate().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorBody = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Plan Create Body");
        Map<String, String> formatMapBody = requestValidationConfig.getSupportResponseCreate().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorBody = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        // ---------------- QUERY PARAM VALIDATION ----------------
        log.trace("Validate mandatory field for Support Response Query Params");

        Map<String, String> formatMapDisplayNameQuery = requestValidationConfig
                .getSupportResponseCreate()
                .getQueryParam()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryQuery = requestValidationConfig
                .getSupportResponseCreate()
                .getQueryParam()
                .stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError validationErrorQuery = CommonRequestValidator.validateMandatoryFields(
                actualParameters,
                mandatoryQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for Support Response Query Params");

        Map<String, Integer> fieldSizeMapQuery = requestValidationConfig
                .getSupportResponseCreate()
                .getQueryParam()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorQuery = CommonRequestValidator.validateFieldSize(
                actualParameters,
                fieldSizeMapQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Support Response Query Params");

        Map<String, String> formatMapQuery = requestValidationConfig
                .getSupportResponseCreate()
                .getQueryParam()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorQuery = CommonRequestValidator.validateFieldValueFormat(
                actualParameters,
                formatMapQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }


    }

    private Map<String, String> getSupportTicketResponseParams(String userId, String targetTicketId, SupportTicketResponseCreateDto supportTicketRequestDto) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.USERID, userId);
        actualParameters.put(AppFieldConstants.TARGET_TICKET_ID, targetTicketId);
        actualParameters.put(AppFieldConstants.RESPONSE_TEXT, supportTicketRequestDto.getResponseText());
        return actualParameters;
    }


    public void validateSupportTicketEdit(String userId, String targetTicketId, SupportTicketRequestEditDto dto) throws ValidationException {

        // 🔄 Collect Params
        Map<String, String> actualParameters = getSupportTicketEditParams(userId, targetTicketId, dto);

        // ---------------- HEADER VALIDATION ----------------
        log.trace("Validate mandatory field for Support Ticket Edit Header");

        Map<String, String> formatMapDisplayNameHeader = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getHeader()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getHeader()
                .stream()
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

        log.trace("Validate Field size for Support Ticket Edit Header");

        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getHeader()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorHeader = CommonRequestValidator.validateFieldSize(
                actualParameters,
                fieldSizeMapHeader,
                formatMapDisplayNameHeader
        );

        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Support Ticket Edit Header");

        Map<String, String> formatMapHeader = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getHeader()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorHeader = CommonRequestValidator.validateFieldValueFormat(
                actualParameters,
                formatMapHeader,
                formatMapDisplayNameHeader
        );

        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ---------------- BODY VALIDATION ----------------
        log.trace("Validate mandatory field for Support Ticket Edit Body");

        Map<String, String> formatMapDisplayNameBody = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getBody()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getBody()
                .stream()
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

        log.trace("Validate Field size for Support Ticket Edit Body");

        Map<String, Integer> fieldSizeMapBody = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getBody()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorBody = CommonRequestValidator.validateFieldSize(
                actualParameters,
                fieldSizeMapBody,
                formatMapDisplayNameBody
        );

        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Support Ticket Edit Body");

        Map<String, String> formatMapBody = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getBody()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorBody = CommonRequestValidator.validateFieldValueFormat(
                actualParameters,
                formatMapBody,
                formatMapDisplayNameBody
        );

        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ---------------- QUERY PARAM VALIDATION ----------------
        log.trace("Validate mandatory field for Support Ticket Edit Query Params");

        Map<String, String> formatMapDisplayNameQuery = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getQueryParam()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryQuery = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getQueryParam()
                .stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError validationErrorQuery = CommonRequestValidator.validateMandatoryFields(
                actualParameters,
                mandatoryQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for Support Ticket Edit Query Params");

        Map<String, Integer> fieldSizeMapQuery = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getQueryParam()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorQuery = CommonRequestValidator.validateFieldSize(
                actualParameters,
                fieldSizeMapQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Support Ticket Edit Query Params");

        Map<String, String> formatMapQuery = requestValidationConfig
                .getSupportTicketRequestEdit()
                .getQueryParam()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorQuery = CommonRequestValidator.validateFieldValueFormat(
                actualParameters,
                formatMapQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }
    }


    private Map<String, String> getSupportTicketEditParams(String userId, String targetTicketId, SupportTicketRequestEditDto dto) {
        Map<String, String> params = new HashMap<>();
        params.put(AppFieldConstants.USERID, userId);
        params.put(AppFieldConstants.TARGET_TICKET_ID, targetTicketId);
        params.put(AppFieldConstants.ISSUE_DESCRIPTION, dto.getIssueDescription());
        params.put(AppFieldConstants.STATUS, dto.getStatus());
        params.put(AppFieldConstants.TICKET_DESCRIPTION, dto.getTicketStatus());
        return params;
    }


    public void validateSupportResponseEdit(String userId, String targetResponseId, SupportTicketResponseEditDto dto) throws ValidationException {

        // 🔄 Collect Params
        Map<String, String> actualParameters = getSupportResponseEditParams(userId, targetResponseId, dto);

        // ---------------- HEADER VALIDATION ----------------
        log.trace("Validate mandatory field for Support Response Edit Header");

        Map<String, String> formatMapDisplayNameHeader = requestValidationConfig
                .getSupportResponseEdit()
                .getHeader()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig
                .getSupportResponseEdit()
                .getHeader()
                .stream()
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

        log.trace("Validate Field size for Support Response Edit Header");

        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig
                .getSupportResponseEdit()
                .getHeader()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorHeader = CommonRequestValidator.validateFieldSize(
                actualParameters,
                fieldSizeMapHeader,
                formatMapDisplayNameHeader
        );

        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Support Response Edit Header");

        Map<String, String> formatMapHeader = requestValidationConfig
                .getSupportResponseEdit()
                .getHeader()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorHeader = CommonRequestValidator.validateFieldValueFormat(
                actualParameters,
                formatMapHeader,
                formatMapDisplayNameHeader
        );

        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorHeader.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ---------------- BODY VALIDATION ----------------
        log.trace("Validate mandatory field for Support Response Edit Body");

        Map<String, String> formatMapDisplayNameBody = requestValidationConfig
                .getSupportResponseEdit()
                .getBody()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig
                .getSupportResponseEdit()
                .getBody()
                .stream()
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

        log.trace("Validate Field size for Support Response Edit Body");

        Map<String, Integer> fieldSizeMapBody = requestValidationConfig
                .getSupportResponseEdit()
                .getBody()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorBody = CommonRequestValidator.validateFieldSize(
                actualParameters,
                fieldSizeMapBody,
                formatMapDisplayNameBody
        );

        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Support Response Edit Body");

        Map<String, String> formatMapBody = requestValidationConfig
                .getSupportResponseEdit()
                .getBody()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorBody = CommonRequestValidator.validateFieldValueFormat(
                actualParameters,
                formatMapBody,
                formatMapDisplayNameBody
        );

        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorBody.getField(), HttpStatus.BAD_REQUEST.value());
        }


        // ---------------- QUERY PARAM VALIDATION ----------------
        log.trace("Validate mandatory field for Support Response Edit Query Params");

        Map<String, String> formatMapDisplayNameQuery = requestValidationConfig
                .getSupportResponseEdit()
                .getQueryParam()
                .stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryQuery = requestValidationConfig
                .getSupportResponseEdit()
                .getQueryParam()
                .stream()
                .filter(RequestComponent::getRequired)
                .map(RequestComponent::getName)
                .collect(Collectors.toSet());

        ValidationError validationErrorQuery = CommonRequestValidator.validateMandatoryFields(
                actualParameters,
                mandatoryQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for Support Response Edit Query Params");

        Map<String, Integer> fieldSizeMapQuery = requestValidationConfig
                .getSupportResponseEdit()
                .getQueryParam()
                .stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));

        validationErrorQuery = CommonRequestValidator.validateFieldSize(
                actualParameters,
                fieldSizeMapQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Support Response Edit Query Params");

        Map<String, String> formatMapQuery = requestValidationConfig
                .getSupportResponseEdit()
                .getQueryParam()
                .stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));

        validationErrorQuery = CommonRequestValidator.validateFieldValueFormat(
                actualParameters,
                formatMapQuery,
                formatMapDisplayNameQuery
        );

        if (validationErrorQuery != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQuery);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError()
                    .reqValidationError(validationErrorQuery.getField(), HttpStatus.BAD_REQUEST.value());
        }
    }

    private Map<String, String> getSupportResponseEditParams(String userId, String targetResponseId, SupportTicketResponseEditDto dto) {
        Map<String, String> params = new HashMap<>();
        params.put(AppFieldConstants.USERID, userId);
        params.put(AppFieldConstants.TARGET_RESPONSE_ID, targetResponseId);
        params.put(AppFieldConstants.RESPONSE_TEXT, dto.getResponseText());
        params.put(AppFieldConstants.STATUS, dto.getStatus());
        return params;
    }


}
