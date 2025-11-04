package com.subscription.subscription_system.validator.basicValidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscription.subscription_system.constants.AppFieldConstants;
import com.subscription.subscription_system.dto.AdminCreateRequestDto;
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
public class AdminValidator {
    private RequestValidationConfig requestValidationConfig;

    private final ApplicationContext applicationContext;

    public AdminValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:support\\json\\admin.json");
        requestValidationConfig = new ObjectMapper().readValue(ResourcesUtils.getResourceContent(resource),
                RequestValidationConfig.class);
    }

    public void validateAdminCreate(AdminCreateRequestDto input) throws CommonException {
        Map<String, String> actualParameters = getAdminCreateParams(input);

        log.trace("Validate mandatory field for creating Admin ");
        Map<String, String> formatMapDisplayName = requestValidationConfig.getAdminCreate().getBody().stream()
                .filter(a -> a.getDisplayName() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getDisplayName));

        Set<String> mandatoryHeader = requestValidationConfig.getAdminCreate().getBody().stream()
                .filter(RequestComponent::getRequired).map(RequestComponent::getName).collect(Collectors.toSet());

        ValidationError validationError = CommonRequestValidator.validateMandatoryFields(actualParameters,
                mandatoryHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.MISSING_MANDATORY_FIELD.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field size for creating Admin");
        Map<String, Integer> fieldSizeMapHeader = requestValidationConfig.getAdminCreate().getBody().stream()
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getMaxLength));
        validationError = CommonRequestValidator.validateFieldSize(actualParameters, fieldSizeMapHeader, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_FIELD_SIZE.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }

        log.trace("Validate Field format for creating Admin");
        Map<String, String> formatMapBody = requestValidationConfig.getAdminCreate().getBody().stream()
                .filter(a -> a.getFormat() != null)
                .collect(Collectors.toMap(RequestComponent::getName, RequestComponent::getFormat));
        validationError = CommonRequestValidator.validateFieldValueFormat(actualParameters, formatMapBody, formatMapDisplayName);
        if (validationError != null) {
            log.warn(ErrorMessages.MSG_VALIDATION, validationError);
            throw ApplicationErrorCode.INVALID_INPUT_FORMAT.getError().reqValidationError(validationError.getField(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private static Map<String, String> getAdminCreateParams(AdminCreateRequestDto input) {
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
        actualParameters.put(AppFieldConstants.SALARY, input.getSalary());
        return actualParameters;
    }
}
