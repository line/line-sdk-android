package com.linecorp.linesdk.internal;

import androidx.annotation.NonNull;

import static com.linecorp.linesdk.utils.DebugUtils.hideIfNotDebug;

/**
 * Immutable data class represents a channel access token.
 * This class has a refresh token. To manage the access token only by LINE SDK, this class should
 * not publish to LINE SDK user.
 */
public class InternalAccessToken {
    @NonNull
    private final String accessToken;
    private final long expiresInMillis;
    private final long issuedClientTimeMillis;
    @NonNull
    private final String refreshToken;

    public InternalAccessToken(
            @NonNull String accessToken,
            long expiresInMillis,
            long issuedClientTimeMillis,
            @NonNull String refreshToken) {
        this.accessToken = accessToken;
        this.expiresInMillis = expiresInMillis;
        this.issuedClientTimeMillis = issuedClientTimeMillis;
        this.refreshToken = refreshToken;
    }

    @NonNull
    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresInMillis() {
        return expiresInMillis;
    }

    public long getIssuedClientTimeMillis() {
        return issuedClientTimeMillis;
    }

    @NonNull
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InternalAccessToken that = (InternalAccessToken) o;

        if (expiresInMillis != that.expiresInMillis)
            return false;
        if (issuedClientTimeMillis != that.issuedClientTimeMillis) return false;
        if (!accessToken.equals(that.accessToken)) return false;
        return refreshToken.equals(that.refreshToken);
    }

    @Override
    public int hashCode() {
        int result = accessToken.hashCode();
        result = 31 * result + (int) (expiresInMillis ^ (expiresInMillis >>> 32));
        result = 31 * result + (int) (issuedClientTimeMillis ^ (issuedClientTimeMillis >>> 32));
        result = 31 * result + refreshToken.hashCode();
        return result;
    }

    // Don't output the access token and refresh token because there is possibility to remain it on log.
    // Be careful not to remove this logic when you regenerate toString().
    @Override
    public String toString() {
        return "InternalAccessToken{" +
               "accessToken='" + hideIfNotDebug(accessToken) + '\'' +
               ", expiresInMillis=" + expiresInMillis +
               ", issuedClientTimeMillis=" + issuedClientTimeMillis +
               ", refreshToken='" + hideIfNotDebug(refreshToken) + '\'' +
               '}';
    }
}
