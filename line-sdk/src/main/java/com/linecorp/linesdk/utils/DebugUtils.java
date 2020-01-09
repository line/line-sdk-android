package com.linecorp.linesdk.utils;

import com.linecorp.linesdk.BuildConfig;

/**
 * Utility class for Debug.
 */
public final class DebugUtils {
    private DebugUtils() { }

    public static Object hideIfNotDebug(final Object value) {
        return BuildConfig.DEBUG ? value : "#####";
    }
}
