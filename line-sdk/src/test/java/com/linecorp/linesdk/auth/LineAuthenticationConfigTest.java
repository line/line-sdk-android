package com.linecorp.linesdk.auth;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.ManifestParser;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.api.LineDefaultEnvConfig;
import com.linecorp.linesdk.api.LineEnvConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link LineAuthenticationConfig}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineAuthenticationConfigTest {

    private static final String TEST_API_SERVER_BASE_URI = "https://api-test";

    private static class TestEnvConfig extends LineEnvConfig {
        @NonNull
        @Override
        public String getApiServerBaseUri() {
            return TEST_API_SERVER_BASE_URI;
        }
    }

    @Test
    public void testParcelable() {
        LineAuthenticationConfig expected = new LineAuthenticationConfig.Builder("1")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
                .disableLineAppAuthentication()
                .disableEncryptorPreparation()
                .build();

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        LineAuthenticationConfig actual = LineAuthenticationConfig.CREATOR.createFromParcel(parcel);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testEquals() {
        LineAuthenticationConfig expected = new LineAuthenticationConfig.Builder("1")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
                .build();

        assertTrue(expected.equals(new LineAuthenticationConfig.Builder("1")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
                .build()));
        assertFalse(expected.equals(new LineAuthenticationConfig.Builder("2")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
                .build()));
        assertFalse(expected.equals(new LineAuthenticationConfig.Builder("1")
               .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration/test"))
               .apiBaseUrl(Uri.parse("https://line.me"))
               .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
               .build()));
        assertFalse(expected.equals(new LineAuthenticationConfig.Builder("1")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me/test"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
                .build()));
        assertFalse(expected.equals(new LineAuthenticationConfig.Builder("1")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin/test"))
                .build()));
        assertFalse(expected.equals(new LineAuthenticationConfig.Builder("1")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
                .disableLineAppAuthentication()
                .build()));
        assertFalse(expected.equals(new LineAuthenticationConfig.Builder("1")
                .openidDiscoveryDocumentUrl(Uri.parse("https://line.me/.well-known/openid-configuration"))
                .apiBaseUrl(Uri.parse("https://line.me"))
                .webLoginPageUrl(Uri.parse("https://line.me/weblogin"))
                .disableEncryptorPreparation()
                .build()));
    }

    @Test
    public void testLineAuthenticationConfigBuilder() {
        String channelId = "test_channel_id";
        Context mockContext = Mockito.mock(Context.class);
        ManifestParser mockManifestParser = Mockito.mock(ManifestParser.class);
        LineAuthenticationConfig.Builder builder;

        // The default one.
        Mockito.when(mockManifestParser.parse(mockContext))
                .thenReturn(new LineDefaultEnvConfig());
        builder = new LineAuthenticationConfig.Builder(channelId, mockContext, mockManifestParser);

        Assert.assertEquals(
                Uri.parse(new LineDefaultEnvConfig().getApiServerBaseUri()),
                builder.build().getApiBaseUrl()
        );

        // A custom one.
        Mockito.when(mockManifestParser.parse(mockContext))
                .thenReturn(new TestEnvConfig());
        builder = new LineAuthenticationConfig.Builder(channelId, mockContext, mockManifestParser);

        Assert.assertEquals(Uri.parse(TEST_API_SERVER_BASE_URI), builder.build().getApiBaseUrl());

        // Test that it's still can be overwritten.
        String anotherTestEnvApiBaseUrl = "https://another_test_env_api_base_url";
        builder.apiBaseUrl(Uri.parse(anotherTestEnvApiBaseUrl));

        Assert.assertEquals(Uri.parse(anotherTestEnvApiBaseUrl), builder.build().getApiBaseUrl());
    }

}
