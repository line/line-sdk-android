package com.linecorp.linesdk.utils;

public final class ObjectUtils {
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }
}
