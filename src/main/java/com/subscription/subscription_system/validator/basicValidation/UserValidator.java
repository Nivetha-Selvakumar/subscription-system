package com.subscription.subscription_system.validator.basicValidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.subscription_system.constants.AppFieldConstants;
import com.subscription.subscription_system.dto.LoginRequestDto;
import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.dto.UserDetailsRequestDto;
import com.subscription.subscription_system.dto.UserEditRequestDto;
import com.subscription.subscription_system.exception.ApplicationErrorCode;
import com.subscription.subscription_system.exception.CommonException;
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

    public void validateUserSignup(UserCreateRequestDto input) throws CommonException {
        Map<String, String> actualParameters = getUserSignupParams(input);

        log.trace("Validate mandatory field for Signup User ");
        Map<String, String> formatMapDisplayName = requestValidationConfig.getUserSignup().getBody().stream()
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

        log.trace("Validate Field size for Signup User");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getUserSignup().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationError = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Signup User");
        Map<String, String> formatMapBody = requestValidationConfig.getUserSignup().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationError = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private static Map<String, String> getUserSignupParams(UserCreateRequestDto input) {
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
        actualParameters.put(AppFieldConstants.STATUS, input.getStatus());
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


    public void validateEditUser(String userId, String targetUserId, UserEditRequestDto editDto) throws ValidationException {

        // Header
        Map<String, String> actualParameters = userEditParams(userId,targetUserId,editDto);

        log.trace("Validate mandatory field for Edit Users Header ");
        Map<String, String> formatMapDisplayNameHeader = requestValidationConfig.getUserEdit().getHeader().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getUserEdit().getHeader().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationErrorHeader = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationErrorHeader.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size  for Edit Users Header");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getUserEdit().getHeader().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationErrorHeader = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationErrorHeader.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Edit Users Header");
        Map<String, String> formatMapHeader = requestValidationConfig.getUserEdit().getHeader().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationErrorHeader = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationErrorHeader.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }


        // PathVariable
        log.trace("Validate mandatory field for Edit Users Path Variable ");
        Map<String, String> formatMapDisplayNamePathVariable = requestValidationConfig.getUserEdit().getPathVariable().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryPathVariable = requestValidationConfig.getUserEdit().getPathVariable().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationErrorPathVariable = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryPathVariable, formatMapDisplayNamePathVariable);
        if (validationErrorPathVariable != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorPathVariable);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationErrorPathVariable.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size  for Edit Users Path Variable");
        Map<String, Integer> fieldSizeMapPathVariable = requestValidationConfig.getUserEdit().getPathVariable().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationErrorPathVariable = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapPathVariable, formatMapDisplayNamePathVariable);
        if (validationErrorPathVariable != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorPathVariable);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationErrorPathVariable.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Edit Users Path Variable");
        Map<String, String> formatMapPathVariable = requestValidationConfig.getUserEdit().getPathVariable().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationErrorPathVariable = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapPathVariable, formatMapDisplayNamePathVariable);
        if (validationErrorPathVariable != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorPathVariable);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationErrorPathVariable.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }


        // Body
        log.trace("Validate mandatory field for Edit Users Body ");
        Map<String, String> formatMapDisplayNameBody = requestValidationConfig.getUserEdit().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryBody = requestValidationConfig.getUserEdit().getBody().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationErrorBody = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationErrorBody.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size  for Edit Users Body");
        Map<String, Integer> fieldSizeMapBody = requestValidationConfig.getUserEdit().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationErrorBody = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationErrorBody.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Edit Users Body");
        Map<String, String> formatMapBody = requestValidationConfig.getUserEdit().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationErrorBody = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayNameBody);
        if (validationErrorBody != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorBody);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationErrorBody.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private Map<String, String> userEditParams(String userId, String targetUserId, UserEditRequestDto editDto) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.USERID, userId);
        actualParameters.put(AppFieldConstants.TARGET_ID, targetUserId);

        actualParameters.put(AppFieldConstants.FIRST_NAME, editDto.getFirstName());
        actualParameters.put(AppFieldConstants.LAST_NAME, editDto.getLastName());
        actualParameters.put(AppFieldConstants.EMAIL, editDto.getEmail());
        actualParameters.put(AppFieldConstants.PASSWORD, editDto.getPassword());
        actualParameters.put(AppFieldConstants.ADDRESS, editDto.getAddress());
        actualParameters.put(AppFieldConstants.DATE_OF_BIRTH, editDto.getDateOfBirth());
        actualParameters.put(AppFieldConstants.PHONE_NUMBER, editDto.getPhoneNumber());
        actualParameters.put(AppFieldConstants.SEX, editDto.getSex());
        actualParameters.put(AppFieldConstants.ROLE, editDto.getRole());
        actualParameters.put(AppFieldConstants.STATUS, editDto.getStatus());

        return actualParameters;
    }


    public void validateDeleteUser(String userId, String targetUserId) throws ValidationException {

        // Header
        Map<String, String> actualParameters = userDeleteParams(userId,targetUserId);

        log.trace("Validate mandatory field for Delete Users Header ");
        Map<String, String> formatMapDisplayNameHeader = requestValidationConfig.getUserDelete().getHeader().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getUserDelete().getHeader().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationErrorHeader = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationErrorHeader.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size  for Delete Users Header");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getUserDelete().getHeader().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationErrorHeader = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationErrorHeader.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Delete Users Header");
        Map<String, String> formatMapHeader = requestValidationConfig.getUserDelete().getHeader().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationErrorHeader = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapHeader, formatMapDisplayNameHeader);
        if (validationErrorHeader != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorHeader);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationErrorHeader.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }


        // PathVariable
        log.trace("Validate mandatory field for Delete Users Path Variable ");
        Map<String, String> formatMapDisplayNamePathVariable = requestValidationConfig.getUserDelete().getPathVariable().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryPathVariable = requestValidationConfig.getUserDelete().getPathVariable().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationErrorPathVariable = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryPathVariable, formatMapDisplayNamePathVariable);
        if (validationErrorPathVariable != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorPathVariable);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationErrorPathVariable.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size  for Edit Delete Path Variable");
        Map<String, Integer> fieldSizeMapPathVariable = requestValidationConfig.getUserDelete().getPathVariable().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationErrorPathVariable = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapPathVariable, formatMapDisplayNamePathVariable);
        if (validationErrorPathVariable != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorPathVariable);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationErrorPathVariable.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for Edit Delete Path Variable");
        Map<String, String> formatMapPathVariable = requestValidationConfig.getUserDelete().getPathVariable().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationErrorPathVariable = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapPathVariable, formatMapDisplayNamePathVariable);
        if (validationErrorPathVariable != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorPathVariable);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationErrorPathVariable.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private Map<String, String> userDeleteParams(String userId, String targetUserId) {
        Map<String, String> actualParameters = new HashMap<>();
        actualParameters.put(AppFieldConstants.USERID, userId);
        actualParameters.put(AppFieldConstants.TARGET_ID, targetUserId);

        return actualParameters;
    }

    public void validateUserCreate(UserCreateRequestDto input, String userId) throws CommonException {
        Map<String, String> actualParameters = getUserCreateParams(input,userId);

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

        log.trace("Validate mandatory field for Queryparam create User ");
        Map<String, String> formatMapDisplayNameQueryParam = requestValidationConfig.getUserCreate().getQueryParam().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeaderQueryParam = requestValidationConfig.getUserCreate().getQueryParam().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationErrorQueryParam = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeaderQueryParam, formatMapDisplayNameQueryParam);
        if (validationErrorQueryParam != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQueryParam);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationErrorQueryParam.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for creating User");
        Map<String, Integer> fieldSizeMapHeaderQueryParam = requestValidationConfig.getUserCreate().getQueryParam().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationErrorQueryParam = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeaderQueryParam, formatMapDisplayNameQueryParam);
        if (validationErrorQueryParam != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQueryParam);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationErrorQueryParam.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for creating User");
        Map<String, String> formatMapBodyQueryParam = requestValidationConfig.getUserCreate().getQueryParam().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationErrorQueryParam = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBodyQueryParam, formatMapDisplayNameQueryParam);
        if (validationErrorQueryParam != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationErrorQueryParam);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationErrorQueryParam.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private static Map<String, String> getUserCreateParams(UserCreateRequestDto input, String userId) {
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
        actualParameters.put(AppFieldConstants.STATUS, input.getStatus());

        actualParameters.put(AppFieldConstants.USERID, userId);
        return actualParameters;
    }
}
