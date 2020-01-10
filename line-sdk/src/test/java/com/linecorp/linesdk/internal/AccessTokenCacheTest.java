package com.linecorp.linesdk.internal;

import android.content.Context;

import com.linecorp.android.security.encryption.StringCipher;
import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.TestStringCipher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for {@link AccessTokenCache}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class AccessTokenCacheTest {
    private static final String CHANNEL_ID1 = "1";
    private static final String CHANNEL_ID2 = "2";

    private StringCipher encryptor;

    @Before
    public void setUp() {
        encryptor = new TestStringCipher();
    }

    @Test
    public void test() {
        Context context = RuntimeEnvironment.application;
        AccessTokenCache cache1 = new AccessTokenCache(context, CHANNEL_ID1, encryptor);
        AccessTokenCache cache2 = new AccessTokenCache(context, CHANNEL_ID2, encryptor);

        assertNull(cache1.getAccessToken());
        assertNull(cache2.getAccessToken());

        cache1.saveAccessToken(
                new InternalAccessToken("accessToken1", Long.MIN_VALUE, Long.MAX_VALUE, "refreshToken1"));
        cache2.saveAccessToken(
                new InternalAccessToken("accessToken2", 2, 10, "refreshToken2"));

        assertEquals(
                new InternalAccessToken("accessToken1", Long.MIN_VALUE, Long.MAX_VALUE, "refreshToken1"),
                cache1.getAccessToken());
        assertEquals(
                new InternalAccessToken("accessToken2", 2, 10, "refreshToken2"),
                cache2.getAccessToken());

        cache1.clear();

        assertNull(cache1.getAccessToken());
        assertEquals(
                new InternalAccessToken("accessToken2", 2, 10, "refreshToken2"),
                cache2.getAccessToken());
    }
}
