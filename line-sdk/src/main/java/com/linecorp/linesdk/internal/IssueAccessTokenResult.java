package com.linecorp.linesdk.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.Scope;

import java.util.Collections;
import java.util.List;

import static com.linecorp.linesdk.utils.DebugUtils.hideIfNotDebug;

/**
 * Immutable data class represents a result of LineAuthenticationApiClient::issueAccessToken
 */
public class IssueAccessTokenResult {
    @NonNull
    private final InternalAccessToken accessToken;
    @NonNull
    private final List<Scope> scopes;
    @Nullable
    private final LineIdToken idToken;

    public IssueAccessTokenResult(
            @NonNull InternalAccessToken accessToken, @NonNull List<Scope> scopes, @Nullable LineIdToken idToken) {
        this.accessToken = accessToken;
        this.scopes = Collections.unmodifiableList(scopes);
        this.idToken = idToken;
    }

    @NonNull
    public InternalAccessToken getAccessToken() {
        return accessToken;
    }

    @NonNull
    public List<Scope> getScopes() {
        return scopes;
    }

    @Nullable
    public LineIdToken getIdToken() {
        return idToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        IssueAccessTokenResult that = (IssueAccessTokenResult) o;

        if (!accessToken.equals(that.accessToken)) { return false; }
        if (!scopes.equals(that.scopes)) { return false; }
        return idToken != null ? idToken.equals(that.idToken) : that.idToken == null;
    }

    @Override
    public int hashCode() {
        int result = accessToken.hashCode();
        result = 31 * result + scopes.hashCode();
        result = 31 * result + (idToken != null ? idToken.hashCode() : 0);
        return result;
    }

    // Don't output the access token because there is possibility to remain it on log.
    // Be careful not to remove this logic when you regenerate toString().
    @Override
    public String toString() {
        return "IssueAccessTokenResult{" +
               "accessToken=" + hideIfNotDebug(accessToken) +
               ", scopes=" + scopes +
               ", idToken=" + idToken +
               '}';
    }
}
