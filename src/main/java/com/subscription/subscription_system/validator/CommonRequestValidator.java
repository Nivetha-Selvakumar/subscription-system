package com.subscription.subscription_system.validator;

import com.subscription.subscription_system.exception.ErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CommonRequestValidator {

    private static final Logger log = LoggerFactory.getLogger(CommonRequestValidator.class);

    protected CommonRequestValidator() {
    }

    public static ValidationError validateMandatoryFields(Map<String, String> actualParameters,
                                                          Set<String> expectedParameters, Map<String, String> formatMapDisplayName) {
        for (String expected : expectedParameters) {
            if (!actualParameters.containsKey(expected) || actualParameters.get(expected) == null || actualParameters.get(expected).isBlank()) {
                log.error(ErrorMessages.MSG_MANDATORY, expected);
                return new ValidationError(RequestValidatorTypes.MANDATORY.type, formatMapDisplayName.get(expected),
                        RequestValidatorTypes.MANDATORY.errorMessage);
            }
        }
        return null;
    }

    public static ValidationError validateFieldSize(Map<String, String> actualParameters,
                                                    Map<String, Integer> fieldSizeMap, Map<String, String> formatMapDisplayName) {
        for (Map.Entry<String, String> expected : actualParameters.entrySet()) {
            if (fieldSizeMap.containsKey(expected.getKey()) && actualParameters.get(expected.getKey()) != null && fieldSizeMap.get(expected.getKey()) < actualParameters.get(expected.getKey()).length()) {
                log.error(ErrorMessages.MSG_FIELD_SIZE, expected.getKey());
                return new ValidationError(RequestValidatorTypes.SIZE.type,formatMapDisplayName.get(expected.getKey()), RequestValidatorTypes.SIZE.errorMessage);
            }
        }
        return null;
    }

    public static ValidationError validateFieldValueFormat(Map<String, String> actualParameters, Map<String, String> formatMap, Map<String, String> formatMapDisplayName) {
        for (Map.Entry<String, String> expected : actualParameters.entrySet()) {
            if (formatMap.containsKey(expected.getKey()) && actualParameters.get(expected.getKey()) != null && !Pattern.matches(formatMap.get(expected.getKey()), actualParameters.get(expected.getKey()))) {
                log.error(ErrorMessages.MSG_FIELD_FORMAT, expected.getKey());
                return new ValidationError(RequestValidatorTypes.FORMAT.type, formatMapDisplayName.get(expected.getKey()), RequestValidatorTypes.FORMAT.errorMessage);
            }
        }
        return null;
    }

}
