package com.linecorp.linesdk.internal.nwclient;

import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.TestConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;

/**
 * Test for {@link IdTokenValidator}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class IdTokenValidatorTest {
    private static final String ISSUER = "https://access.line.me";
    private static final String CHANNEL_ID = "testChannelId";
    private static final String USER_ID = "testUserId";
    private static final String NONCE = "testNonce";

    private static final Date NOW = new Date();
    private static final Date ONE_HOUR_LATER = new Date(NOW.getTime() + 60 * 60 * 1000);
    private static final Date SIX_MINUTE_LATER = new Date(NOW.getTime() + 6 * 60 * 1000);
    private static final Date SIX_MINUTES_EARLIER = new Date(NOW.getTime() - 6 * 60 * 1000);

    private static IdTokenValidator buildValidator(final LineIdToken idToken) {
        return new IdTokenValidator.Builder()
                .idToken(idToken)
                .expectedIssuer(ISSUER)
                .expectedUserId(USER_ID)
                .expectedChannelId(CHANNEL_ID)
                .expectedNonce(NONCE)
                .build();
    }

    private static void validateFail(final LineIdToken idToken, final String expectedErrorMessage) {
        try {
            final IdTokenValidator validator = buildValidator(idToken);
            validator.validate();
            fail("should fail here");
        } catch (final Exception e) {
            assertThat(e.getMessage(), startsWith(expectedErrorMessage));
        }
    }

    @Test
    public void testSuccess() {
        final LineIdToken idToken = new LineIdToken
                .Builder()
                .issuer(ISSUER)
                .subject(USER_ID)
                .audience(CHANNEL_ID)
                .issuedAt(NOW)
                .expiresAt(ONE_HOUR_LATER)
                .nonce(NONCE)
                .build();

        final IdTokenValidator validator = buildValidator(idToken);

        validator.validate();
    }

    @Test
    public void testFail_Issuer() {
        final LineIdToken idToken = new LineIdToken
                .Builder()
                .issuer("badIssuer")
                .subject(USER_ID)
                .audience(CHANNEL_ID)
                .issuedAt(NOW)
                .expiresAt(ONE_HOUR_LATER)
                .nonce(NONCE)
                .build();

        validateFail(idToken, "OpenId issuer does not match.");
    }

    @Test
    public void testFail_Subject() {
        final LineIdToken idToken = new LineIdToken
                .Builder()
                .issuer(ISSUER)
                .subject("badUserId")
                .audience(CHANNEL_ID)
                .issuedAt(NOW)
                .expiresAt(ONE_HOUR_LATER)
                .nonce(NONCE)
                .build();

        validateFail(idToken, "OpenId subject does not match.");
    }

    @Test
    public void testFail_Audience() {
        final LineIdToken idToken = new LineIdToken
                .Builder()
                .issuer(ISSUER)
                .subject(USER_ID)
                .audience("badChannelId")
                .issuedAt(NOW)
                .expiresAt(ONE_HOUR_LATER)
                .nonce(NONCE)
                .build();

        validateFail(idToken, "OpenId audience does not match.");
    }

    @Test
    public void testFail_Nonce() {
        final LineIdToken idToken = new LineIdToken
                .Builder()
                .issuer(ISSUER)
                .subject(USER_ID)
                .audience(CHANNEL_ID)
                .issuedAt(NOW)
                .expiresAt(ONE_HOUR_LATER)
                .nonce("badNonce")
                .build();

        validateFail(idToken, "OpenId nonce does not match.");
    }

    @Test
    public void testFail_IssuedAt() {
        final LineIdToken idToken = new LineIdToken
                .Builder()
                .issuer(ISSUER)
                .subject(USER_ID)
                .audience(CHANNEL_ID)
                .issuedAt(SIX_MINUTE_LATER)
                .expiresAt(ONE_HOUR_LATER)
                .nonce(NONCE)
                .build();

        validateFail(idToken, "OpenId issuedAt is after current time");
    }

    @Test
    public void testFail_ExpiresAt() {
        final LineIdToken idToken = new LineIdToken
                .Builder()
                .issuer(ISSUER)
                .subject(USER_ID)
                .audience(CHANNEL_ID)
                .issuedAt(NOW)
                .expiresAt(SIX_MINUTES_EARLIER)
                .nonce(NONCE)
                .build();

        validateFail(idToken, "OpenId expiresAt is before current time");
    }
}
