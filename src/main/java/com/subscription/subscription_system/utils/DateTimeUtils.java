package com.subscription.subscription_system.utils;

import com.subscription.subscription_system.exception.ErrorMessages;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeUtils {
    private static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TIME_ZONE = "Asia/Kolkata";

    public static String convertToTimeZoneAndFormat(LocalDateTime localDateTime, String timezone, String format) {

        log.info("Convert to Timezone and format");
        if (localDateTime == null) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_DURING_CONVERSION);
        }

        // Fallback to defaults if timezone or format is null
        timezone = (timezone == null || timezone.isEmpty()) ? DateTimeUtils.DEFAULT_TIME_ZONE : timezone;
        format = (format == null || format.isEmpty()) ? DateTimeUtils.TIME_STAMP_FORMAT : format;

        // Convert LocalDateTime to ZonedDateTime in the target timezone
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of(timezone));
        // Format ZonedDateTime to the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return zonedDateTime.format(formatter);
    }

}
