package com.linecorp.linesdk.auth;

import android.net.Uri;
import android.os.Parcel;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.TestConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link LineAuthenticationConfig}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestConfig.TARGET_SDK_VERSION)
public class LineAuthenticationConfigTest {
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
}
