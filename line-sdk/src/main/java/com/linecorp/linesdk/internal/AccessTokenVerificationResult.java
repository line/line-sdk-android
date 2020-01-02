package com.linecorp.linesdk.internal;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.Scope;

import java.util.Collections;
import java.util.List;

/**
 * Verification result of an access token.
 */
public class AccessTokenVerificationResult {
    @NonNull
    private final String channelId;
    private final long expiresInMillis;
    @NonNull
    private final List<Scope> scopes;

    public AccessTokenVerificationResult(
            @NonNull String channelId,
            long expiresInMillis,
            @NonNull List<Scope> scopes) {
        this.channelId = channelId;
        this.expiresInMillis = expiresInMillis;
        this.scopes = Collections.unmodifiableList(scopes);
    }

    @NonNull
    public String getChannelId() {
        return channelId;
    }

    public long getExpiresInMillis() {
        return expiresInMillis;
    }

    @NonNull
    public List<Scope> getScopes() {
        return scopes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AccessTokenVerificationResult that = (AccessTokenVerificationResult) o;

        if (expiresInMillis != that.expiresInMillis) { return false; }
        if (!channelId.equals(that.channelId)) { return false; }
        return scopes.equals(that.scopes);
    }

    @Override
    public int hashCode() {
        int result = channelId.hashCode();
        result = 31 * result + (int) (expiresInMillis ^ expiresInMillis >>> 32);
        result = 31 * result + scopes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AccessTokenVerificationResult{" +
               "channelId='" + channelId + '\'' +
               ", expiresInMillis=" + expiresInMillis +
               ", scopes=" + scopes +
               '}';
    }
}
