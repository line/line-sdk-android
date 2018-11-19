package com.linecorp.android.security;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.linecorp.linesdk.BuildConfig;

import java.security.SecureRandom;

public final class SecurityUtils {
    private static final SecureRandom secureRandom = new SecureRandom();

    private SecurityUtils() { }

    public static Object hideIfNotDebug(final Object value) {
        return BuildConfig.DEBUG ? value : "#####";
    }

    @NonNull
    public static String createRandomString(final int byteLength) {
        final byte[] byteArray = new byte[byteLength];
        secureRandom.nextBytes(byteArray);
        return Base64.encodeToString(byteArray, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }
}
