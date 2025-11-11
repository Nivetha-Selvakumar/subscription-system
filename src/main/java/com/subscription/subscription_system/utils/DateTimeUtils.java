package com.subscription.subscription_system.utils;

import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.exception.ErrorMessages;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

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

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MMM-dd", Locale.ENGLISH);

    public static LocalDateTime parseToLocalDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        LocalDate localDate = LocalDate.parse(dateStr, FORMATTER);
        return localDate.atStartOfDay();
    }

    public static LocalDate parseFlexibleDate(String input) throws CommonException {
        if (input == null || input.trim().isEmpty()) return null;

        String trimmed = input.trim();

        // Formatter we want to store in: e.g. 2025-NOV-10
        DateTimeFormatter targetFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy-MMM-dd")
                .toFormatter(Locale.ENGLISH);

        // ISO (yyyy-MM-dd) first (very common)
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ignored) {}

        // try the custom formatter (case-insensitive)
        try {
            return LocalDate.parse(trimmed, targetFormatter);
        } catch (Exception ignored) {}

        // try uppercase version as last resort
        try {
            return LocalDate.parse(trimmed.toUpperCase(Locale.ENGLISH), targetFormatter);
        } catch (Exception ex) {
            // give a helpful error
            throw new CommonException("Invalid date format for value '" + input +
                    "'. Expected formats: yyyy-MM-dd or yyyy-MMM-dd (e.g. 2025-11-10 or 2025-NOV-10).",
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    public static String convertToIsoDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;

        try {
            // Handles "2025-NOV-10" or "2025-Nov-10"
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd", Locale.ENGLISH);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateStr, inputFormatter);
            return date.format(outputFormatter); // returns "2025-11-10"
        } catch (Exception e) {
            // fallback: if already yyyy-MM-dd, just return it
            try {
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return dateStr;
            } catch (Exception ignored) {
                return null; // invalid format
            }
        }
    }


}
