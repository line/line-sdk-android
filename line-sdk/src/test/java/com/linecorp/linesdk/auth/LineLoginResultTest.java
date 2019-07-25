package com.linecorp.linesdk.auth;

import android.net.Uri;
import android.os.Parcel;
import androidx.annotation.NonNull;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.TestConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link LineLoginResult}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestConfig.TARGET_SDK_VERSION)
public class LineLoginResultTest {
    @Test
    public void testParcelable() {
        Date now = new Date();
        Date oneHourLater = new Date(now.getTime() + 3600000);

        LineLoginResult expected = new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                createLineProfile(),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                true,
                createLineCredential(),
                createLineApiError());

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        LineLoginResult actual = LineLoginResult.CREATOR.createFromParcel(parcel);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testEquals() {
        Date now = new Date();
        Date oneHourLater = new Date(now.getTime() + 3600000);

        LineLoginResult expected = new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                createLineProfile(),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                true,
                createLineCredential(),
                createLineApiError());

        assertTrue(expected.equals(new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                createLineProfile(),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                true,
                createLineCredential(),
                createLineApiError())));
        assertFalse(expected.equals(new LineLoginResult(
                LineApiResponseCode.CANCEL,
                new LineProfile("id2", "displayName", Uri.parse("http://line.me"), "statusMessage"),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                true,
                createLineCredential(),
                createLineApiError())));
        assertFalse(expected.equals(new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                new LineProfile("id2", "displayName", Uri.parse("http://line.me"), "statusMessage"),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                true,
                createLineCredential(),
                createLineApiError())));
        assertFalse(expected.equals(new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                new LineProfile("id2", "displayName", Uri.parse("http://line.me"), "statusMessage"),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                true,
                createLineCredential(),
                new LineApiError("testErrorMessage2"))));

        assertFalse(expected.equals(new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                createLineProfile(),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                false,
                createLineCredential(),
                createLineApiError())));

        assertFalse(expected.equals(new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                createLineProfile(),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcd")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                null,
                createLineCredential(),
                createLineApiError())));

        assertFalse(expected.equals(new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                createLineProfile(),
                null,
                true,
                createLineCredential(),
                createLineApiError())));

        assertFalse(expected.equals(new LineLoginResult(
                LineApiResponseCode.SUCCESS,
                createLineProfile(),
                new LineIdToken.Builder()
                        .issuer("https://access.line.me")
                        .subject("abcdef")
                        .audience("123456")
                        .issuedAt(now)
                        .expiresAt(oneHourLater)
                        .nonce("qwerty")
                        .name("displayName")
                        .picture("http://line.me")
                        .email("user@example.com")
                        .build(),
                true,
                createLineCredential(),
                createLineApiError())));
    }

    @NonNull
    private LineCredential createLineCredential() {
        return new LineCredential(
                new LineAccessToken("accessToken", 1000, 2000),
                Arrays.asList(Scope.FRIEND, Scope.GROUP));
    }

    @NonNull
    private LineProfile createLineProfile() {
        return new LineProfile("id", "displayName", Uri.parse("http://line.me"), "statusMessage");
    }

    @NonNull
    private LineApiError createLineApiError() {
        return new LineApiError("testErrorMessage");
    }
}
