package com.linecorp.linesdk.utils;

import android.os.Parcel;

import java.util.Date;

/**
 * Utility class to support Parcel read/write operations.
 */
public final class ParcelUtils {
    private static final long TIME_NONE = -1;

    private ParcelUtils() { }

    public static void writeDate(final Parcel dest, final Date val) {
        dest.writeLong(val != null ? val.getTime() : TIME_NONE);
    }

    public static Date readDate(final Parcel in) {
        final long time = in.readLong();
        return time != TIME_NONE ? new Date(time) : null;
    }

    public static <T extends Enum> void writeEnum(final Parcel dest, final T val) {
        dest.writeString(val != null ? val.name() : null);
    }

    public static <T extends Enum<T>> T readEnum(final Parcel in, final Class<T> enumType) {
        final String enumName = in.readString();
        return enumName != null ? T.valueOf(enumType, enumName) : null;
    }
}
