package com.subscription.subscription_system.validator.basicValidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.subscription_system.constants.AppFieldConstants;
import com.subscription.subscription_system.dto.LoginRequestDto;
import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.dto.UserDetailsRequestDto;
import com.subscription.subscription_system.exception.ApplicationErrorCode;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.exception.ErrorMessages;
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
public class UserValidator {
    private RequestValidationConfig requestValidationConfig;

    private final ApplicationContext applicationContext;

    public UserValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:support\\json\\user.json");
        requestValidationConfig = new ObjectMapper().readValue(ResourcesUtils.getResourceContent(resource),
                RequestValidationConfig.class);
    }

    public void validateUserCreate(UserCreateRequestDto input) throws CommonException {
        Map<String, String> actualParameters = getUserCreateParams(input);

        log.trace("Validate mandatory field for creating User ");
        Map<String, String> formatMapDisplayName = requestValidationConfig.getUserCreate().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getUserCreate().getBody().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationError = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for creating User");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getUserCreate().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationError = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for creating User");
        Map<String, String> formatMapBody = requestValidationConfig.getUserCreate().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationError = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private static Map<String, String> getUserCreateParams(UserCreateRequestDto input) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.FIRST_NAME, input.getFirstName());
        actualParameters.put(AppFieldConstants.LAST_NAME, input.getLastName());
        actualParameters.put(AppFieldConstants.EMAIL, input.getEmail());
        actualParameters.put(AppFieldConstants.PASSWORD, input.getPassword());
        actualParameters.put(AppFieldConstants.ADDRESS, input.getAddress());
        actualParameters.put(AppFieldConstants.DATE_OF_BIRTH, input.getDateOfBirth());
        actualParameters.put(AppFieldConstants.PHONE_NUMBER, input.getPhoneNumber());
        actualParameters.put(AppFieldConstants.SEX, input.getSex());
        actualParameters.put(AppFieldConstants.ROLE, input.getRole());
        return actualParameters;
    }

    public void validateLoginUser(LoginRequestDto input) throws CommonException {
        Map<String, String> actualParameters = getUserLoginParams(input);

        log.trace("Validate mandatory field for LoggingIn a User ");
        Map<String, String> formatMapDisplayName = requestValidationConfig.getUserLogin().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getUserLogin().getBody().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationError = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for LoggingIn a User");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getUserLogin().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationError = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for LoggingIn a User");
        Map<String, String> formatMapBody = requestValidationConfig.getUserLogin().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationError = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private Map<String, String> getUserLoginParams(LoginRequestDto input) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.EMAIL, input.getEmail());
        actualParameters.put(AppFieldConstants.PASSWORD, input.getPassword());

        return actualParameters;
    }

    public void validateUserDetails(UserDetailsRequestDto input) throws CommonException {
        Map<String, String> actualParameters = userDetailsParams(input);

        log.trace("Validate mandatory field for user details ");
        Map<String, String> formatMapDisplayName = requestValidationConfig.getUserDetails().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getUserDetails().getBody().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationError = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size  for user details");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getUserDetails().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationError = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format  for user details");
        Map<String, String> formatMapBody = requestValidationConfig.getUserDetails().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationError = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private Map<String, String> userDetailsParams(UserDetailsRequestDto input) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.EMAIL, input.getEmail());
        actualParameters.put(AppFieldConstants.USERID, input.getUserId());

        return actualParameters;
    }

    public void validateUserList(String input) throws CommonException {
        Map<String, String> actualParameters = userListParams(input);

        log.trace("Validate mandatory field for Getting Users ");
        Map<String, String> formatMapDisplayName = requestValidationConfig.getUserList().getHeader().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getUserList().getHeader().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationError = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size  for Getting Users");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getUserList().getHeader().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationError = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Getting Users");
        Map<String, String> formatMapBody = requestValidationConfig.getUserList().getHeader().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationError = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private Map<String, String> userListParams(String input) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.USERID, input);

        return actualParameters;
    }


}
