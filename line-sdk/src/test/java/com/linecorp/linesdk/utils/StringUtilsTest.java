package com.linecorp.linesdk.utils;

import com.linecorp.linesdk.TestConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link StringUtils}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class StringUtilsTest {
    @Test
    public void testCreateRandomAlphaNumeric() {
        testCreateRandomAlphaNumeric(16);
        testCreateRandomAlphaNumeric(32);
        testCreateRandomAlphaNumeric(64);
        testCreateRandomAlphaNumeric(128);
        testCreateRandomAlphaNumeric(256);
        testCreateRandomAlphaNumeric(512);
        testCreateRandomAlphaNumeric(1024);
        testCreateRandomAlphaNumeric(2048);
        testCreateRandomAlphaNumeric(4096);
    }

    private void testCreateRandomAlphaNumeric(final int count) {
        final String actual = StringUtils.createRandomAlphaNumeric(count);
        assertEquals(count, actual.length());

        final String availableChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (final char c : actual.toCharArray()) {
            assertTrue(availableChars.indexOf(c) >= 0);
        }
    }

    @Test
    public void testCreateRandomString() {
        testCreateRandomString("0123456789", 1024);
        testCreateRandomString("abcdefghijklmnopqrstuvwxyz", 1024);
        testCreateRandomString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 1024);
        testCreateRandomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 1024);
    }

    private void testCreateRandomString(final String availableChars, final int count) {
        final String actual = StringUtils.createRandomString(availableChars, count);
        assertEquals(count, actual.length());
        for (final char c : actual.toCharArray()) {
            assertTrue(availableChars.indexOf(c) >= 0);
        }
    }

    @Test
    public void testCreateString() {
        testCreateString('0', '9', "0123456789");
        testCreateString('a', 'z', "abcdefghijklmnopqrstuvwxyz");
        testCreateString('A', 'Z', "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    private void testCreateString(final char startChar, final char endChar, final String expected) {
        final String actual = StringUtils.createString(startChar, endChar);
        assertEquals(expected, actual);
    }
}
