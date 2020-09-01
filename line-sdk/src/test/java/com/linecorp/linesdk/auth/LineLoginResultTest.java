package com.linecorp.linesdk.auth;

import android.net.Uri;
import android.os.Parcel;

import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.LineIdToken.Builder;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.TestConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Date;

import androidx.annotation.NonNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test for {@link LineLoginResult}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineLoginResultTest {
    private final Date now = new Date();
    private final Date oneHourLater = new Date(now.getTime() + 3600000);

    @NonNull
    private static LineCredential createLineCredential() {
        return new LineCredential(
                new LineAccessToken("accessToken", 1000, 2000),
                Arrays.asList(Scope.FRIEND, Scope.GROUP));
    }

    @NonNull
    private static LineProfile createLineProfile() {
        return new LineProfile("id", "displayName", Uri.parse("http://line.me"), "statusMessage");
    }

    @NonNull
    private static LineApiError createLineApiError() {
        return new LineApiError("testErrorMessage");
    }

    @Test
    public void testParcelable() {
        final LineLoginResult expected = loginResultBuilder().build();

        final Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        final LineLoginResult actual = LineLoginResult.CREATOR.createFromParcel(parcel);
        assertEquals(expected, actual);
    }

    @Test
    public void testEquals() {
        final LineLoginResult expected = loginResultBuilder().build();

        // equals
        assertEquals(expected, loginResultBuilder().build());

        // not equals: responseCode
        assertNotEquals(expected, loginResultBuilder()
                .responseCode(LineApiResponseCode.CANCEL)
                .build());

        // not equals: nonce
        assertNotEquals(expected, loginResultBuilder()
                .nonce(null)
                .build());

        assertNotEquals(expected, loginResultBuilder()
                .nonce("differentNonce")
                .build());

        // not equals: lineProfile
        assertNotEquals(expected, loginResultBuilder()
                .lineProfile(null)
                .build());

        assertNotEquals(expected, loginResultBuilder()
                .lineProfile(
                        new LineProfile("id2", "displayName", Uri.parse("http://line.me"),
                                        "statusMessage")
                )
                .build());

        // not equals: lineIdToken
        assertNotEquals(expected, loginResultBuilder()
                .lineIdToken(null)
                .build());

        assertNotEquals(expected, loginResultBuilder()
                .lineIdToken(
                        new Builder()
                                .issuer("https://access.line.me")
                                .subject("abcdef")
                                .audience("123456")
                                .issuedAt(now)
                                .expiresAt(oneHourLater)
                                .nonce("qwerty")
                                .name("displayName")
                                .picture("http://line.me")
                                .email("user@example.com")
                                .build()
                )
                .build());

        // not equals: friendshipStatusChanged
        assertNotEquals(expected, loginResultBuilder()
                .friendshipStatusChanged(null)
                .build());

        assertNotEquals(expected, loginResultBuilder()
                .friendshipStatusChanged(false)
                .build());

        // not equals: friendshipStatusChanged
        assertNotEquals(expected, loginResultBuilder()
                .lineCredential(null)
                .build());

        assertNotEquals(expected, loginResultBuilder()
                .lineCredential(
                        new LineCredential(
                                new LineAccessToken("accessToken-xxx", 1000, 2000),
                                Arrays.asList(Scope.FRIEND, Scope.GROUP, Scope.OPENID_CONNECT,
                                              Scope.OC_EMAIL))
                )
                .build());

        // not equals: errorData
        assertNotEquals(expected, loginResultBuilder()
                .errorData(null)
                .build());

        assertNotEquals(expected, loginResultBuilder()
                .errorData(new LineApiError("testErrorMessage2"))
                .build());
    }

    @NonNull
    private LineLoginResult.Builder loginResultBuilder() {
        return new LineLoginResult.Builder()
                .responseCode(LineApiResponseCode.SUCCESS)
                .nonce("loginNonce")
                .lineProfile(createLineProfile())
                .lineIdToken(
                        new LineIdToken.Builder()
                                .rawString("idTokenRawString")
                                .issuer("https://access.line.me")
                                .subject("abcd")
                                .audience("123456")
                                .issuedAt(now)
                                .expiresAt(oneHourLater)
                                .nonce("qwerty")
                                .name("displayName")
                                .picture("http://line.me")
                                .email("user@example.com")
                                .build()
                )
                .friendshipStatusChanged(true)
                .lineCredential(createLineCredential())
                .errorData(createLineApiError());
    }
}
