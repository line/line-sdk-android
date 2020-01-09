package com.linecorp.linesdk.internal.pkce;

import androidx.annotation.NonNull;

/**
 * Code Challenge Method. <br></br>
 * for more details, please refer to: <a href="https://tools.ietf.org/html/rfc7636#section-4.3">RFC 7636: Proof Key for Code Exchange - Section 4.3</a>
 */
public enum CodeChallengeMethod {
    PLAIN("plain"), // not used
    S256("S256"); // always use S256 in LINE SDK

    @NonNull
    private final String value;

    CodeChallengeMethod(@NonNull final String value) {
        this.value = value;
    }

    @NonNull
    public String getValue() {
        return value;
    }
}
