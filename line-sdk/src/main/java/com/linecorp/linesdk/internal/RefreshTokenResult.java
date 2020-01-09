package com.linecorp.linesdk.internal;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.Scope;

import java.util.List;

import static com.linecorp.linesdk.utils.DebugUtils.hideIfNotDebug;

/**
 * Data class represents a result of refresh token.
 */
public class RefreshTokenResult {
    @NonNull
    private final String accessToken;
    private final long expiresInMillis;
    @NonNull
    private final String refreshToken;
    @NonNull
    private final List<Scope> scopes;

    public RefreshTokenResult(
            @NonNull String accessToken,
            long expiresInMillis,
            @NonNull String refreshToken,
            @NonNull List<Scope> scopes) {
        this.accessToken = accessToken;
        this.expiresInMillis = expiresInMillis;
        this.refreshToken = refreshToken;
        this.scopes = scopes;
    }

    @NonNull
    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresInMillis() {
        return expiresInMillis;
    }

    @NonNull
    public String getRefreshToken() {
        return refreshToken;
    }

    @NonNull
    public List<Scope> getScopes() {
        return scopes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        RefreshTokenResult that = (RefreshTokenResult) o;

        if (expiresInMillis != that.expiresInMillis) { return false; }
        if (!accessToken.equals(that.accessToken)) { return false; }
        if (!refreshToken.equals(that.refreshToken)) { return false; }
        return scopes.equals(that.scopes);
    }

    @Override
    public int hashCode() {
        int result = accessToken.hashCode();
        result = 31 * result + (int) (expiresInMillis ^ expiresInMillis >>> 32);
        result = 31 * result + refreshToken.hashCode();
        result = 31 * result + scopes.hashCode();
        return result;
    }

    // Don't output the access token and refresh token because there is possibility to remain it on log.
    // Be careful not to remove this logic when you regenerate toString().
    @Override
    public String toString() {
        return "RefreshTokenResult{" +
               "accessToken='" + hideIfNotDebug(accessToken) + '\'' +
               ", expiresInMillis=" + expiresInMillis +
               ", refreshToken='" + hideIfNotDebug(refreshToken) + '\'' +
               ", scopes=" + scopes +
               '}';
    }
}
