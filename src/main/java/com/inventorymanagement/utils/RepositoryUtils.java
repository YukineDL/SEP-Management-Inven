package com.inventorymanagement.utils;

import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RepositoryUtils {
    public static <T> T setValue(Object object, Class<T> targetType) {
        if (object == null) {
            return null; // or return default value depending on the type
        }

        // Check the target type and cast accordingly
        if (targetType == String.class) {
            return targetType.cast(object instanceof String ? object : StringUtils.EMPTY);
        }

        if (targetType == Double.class) {
            return targetType.cast(object instanceof Double ? object : 0);
        }
        if (targetType == Number.class) {
            return targetType.cast(object instanceof Number ? object : 0);
        }

        if (targetType == Integer.class) {
            return targetType.cast(object instanceof Integer ? object : 0);
        }

        if (targetType == Long.class) {
            return targetType.cast(object instanceof Long ? object : 0);
        }

        if (targetType == Boolean.class) {
            return targetType.cast(object instanceof Boolean ? object : null);
        }

        if (targetType == LocalDate.class) {
            if (object instanceof Date) {
                LocalDate localDate = ((Date) object).toLocalDate();
                return targetType.cast(localDate);
            }
            return null;
        }

        if (targetType == LocalDateTime.class) {
            if (object instanceof Timestamp) {
                LocalDateTime localDateTime = ((Timestamp) object).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                return targetType.cast(localDateTime);
            }
            return null;
        }

        // Add more type checks as needed

        // If no matching type found, return null (or throw exception depending on requirements)
        return null;
    }
}
