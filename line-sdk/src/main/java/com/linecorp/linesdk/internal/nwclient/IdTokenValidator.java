package com.linecorp.linesdk.internal.nwclient;

import com.linecorp.linesdk.LineIdToken;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class IdTokenValidator {
    // allowed clock skew: 5 minutes
    private static final long ALLOWED_CLOCK_SKEW_MILLISECONDS = TimeUnit.MINUTES.toMillis(5);

    private final LineIdToken idToken;

    private final String expectedIssuer;

    private final String expectedUserId;

    private final String expectedChannelId;

    private final String expectedNonce;

    private IdTokenValidator(final Builder builder) {
        idToken = builder.idToken;
        expectedIssuer = builder.expectedIssuer;
        expectedUserId = builder.expectedUserId;
        expectedChannelId = builder.expectedChannelId;
        expectedNonce = builder.expectedNonce;
    }

    private static void notMatchedError(final String errorMsg, final Object expected, final Object actual) {
        throw new RuntimeException(errorMsg
                                   + " expected: " + expected + ", "
                                   + "but received: " + actual);
    }

    public void validate() {
        validateIssuer();
        validateSubject();
        validateAudience();
        validateNonce();
        validateTimestamp();
    }

    private void validateIssuer() {
        final String receivedIssuer = idToken.getIssuer();

        if (expectedIssuer.equals(receivedIssuer)) {
            return;
        }

        notMatchedError("OpenId issuer does not match.", expectedIssuer, receivedIssuer);
    }

    private void validateSubject() {
        final String receivedSubject = idToken.getSubject();

        if (expectedUserId == null) {
            // can not check
            return;
        }

        if (expectedUserId.equals(receivedSubject)) {
            return;
        }

        notMatchedError("OpenId subject does not match.", expectedUserId, receivedSubject);
    }

    private void validateAudience() {
        final String receivedAudience = idToken.getAudience();

        if (expectedChannelId.equals(receivedAudience)) {
            return;
        }

        notMatchedError("OpenId audience does not match.", expectedChannelId, receivedAudience);
    }

    private void validateNonce() {
        final String receivedNonce = idToken.getNonce();

        if (expectedNonce == null && receivedNonce == null) {
            return;
        }

        if (expectedNonce != null && expectedNonce.equals(receivedNonce)) {
            return;
        }

        notMatchedError("OpenId nonce does not match.", expectedNonce, receivedNonce);
    }

    private void validateTimestamp() {
        final Date now = new Date();

        if (idToken.getIssuedAt().getTime() > (now.getTime() + ALLOWED_CLOCK_SKEW_MILLISECONDS)) {
            // issuedAt is after current time
            throw new RuntimeException("OpenId issuedAt is after current time: " + idToken.getIssuedAt());
        }

        if (idToken.getExpiresAt().getTime() < (now.getTime() - ALLOWED_CLOCK_SKEW_MILLISECONDS)) {
            // expiresAt is before current time
            throw new RuntimeException("OpenId expiresAt is before current time: " + idToken.getExpiresAt());
        }
    }

    public static final class Builder {
        private LineIdToken idToken;
        private String expectedIssuer;
        private String expectedUserId;
        private String expectedChannelId;
        private String expectedNonce;

        public Builder() {}

        public Builder idToken(final LineIdToken idToken) {
            this.idToken = idToken;
            return this;
        }

        public Builder expectedIssuer(final String expectedIssuer) {
            this.expectedIssuer = expectedIssuer;
            return this;
        }

        public Builder expectedUserId(final String expectedUserId) {
            this.expectedUserId = expectedUserId;
            return this;
        }

        public Builder expectedChannelId(final String expectedChannelId) {
            this.expectedChannelId = expectedChannelId;
            return this;
        }

        public Builder expectedNonce(final String expectedNonce) {
            this.expectedNonce = expectedNonce;
            return this;
        }

        public IdTokenValidator build() {
            return new IdTokenValidator(this);
        }
    }
}
