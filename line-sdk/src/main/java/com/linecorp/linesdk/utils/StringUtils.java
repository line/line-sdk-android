package com.linecorp.linesdk.utils;

import androidx.annotation.NonNull;

import java.security.SecureRandom;

/**
 * Utility class to support String operations.
 */
public final class StringUtils {
    private static final String LOWERCASE_ALPHABETIC_CHARS = createString('a', 'z');

    private static final String UPPERCASE_ALPHABETIC_CHARS = createString('A', 'Z');

    private static final String NUMERIC_CHARS = createString('0', '9');

    private static final String ALPHABETIC_CHARS = LOWERCASE_ALPHABETIC_CHARS + UPPERCASE_ALPHABETIC_CHARS;

    private static final String ALPHA_NUMERIC_CHARS = ALPHABETIC_CHARS + NUMERIC_CHARS;

    @SuppressWarnings("checkstyle:ConstantName")
    private static final SecureRandom secureRandom = new SecureRandom();

    private StringUtils() { }

    @NonNull
    public static String createRandomAlphaNumeric(final int count) {
        return createRandomString(ALPHA_NUMERIC_CHARS, count);
    }

    @NonNull
    public static String createRandomString(final String availableChars, final int count) {
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            int idx = secureRandom.nextInt(availableChars.length());
            char nextChar = availableChars.charAt(idx);
            result.append(nextChar);
        }

        return result.toString();
    }

    @NonNull
    public static String createString(final char startChar, final char endChar) {
        final StringBuilder result = new StringBuilder();

        for (char c = startChar; c <= endChar; c++) {
            result.append(c);
        }

        return result.toString();
    }
}
