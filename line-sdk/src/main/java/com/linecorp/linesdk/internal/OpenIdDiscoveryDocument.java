package com.linecorp.linesdk.internal;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * LINE Login Discovery Document. <br></br>
 * Please refer to Specification: <a href="http://openid.net/specs/openid-connect-discovery-1_0.html">OpenID Connect Discovery 1.0</a>
 */
public class OpenIdDiscoveryDocument {
    @NonNull
    private final String issuer;

    @NonNull
    private final String authorizationEndpoint;

    @NonNull
    private final String tokenEndpoint;

    @NonNull
    private final String jwksUri;

    @NonNull
    private final List<String> responseTypesSupported;

    @NonNull
    private final List<String> subjectTypesSupported;

    @NonNull
    private final List<String> idTokenSigningAlgValuesSupported;

    private OpenIdDiscoveryDocument(final Builder builder) {
        issuer = builder.issuer;
        authorizationEndpoint = builder.authorizationEndpoint;
        tokenEndpoint = builder.tokenEndpoint;
        jwksUri = builder.jwksUri;
        responseTypesSupported = builder.responseTypesSupported;
        subjectTypesSupported = builder.subjectTypesSupported;
        idTokenSigningAlgValuesSupported = builder.idTokenSigningAlgValuesSupported;
    }

    @NonNull
    public String getIssuer() {
        return issuer;
    }

    @NonNull
    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    @NonNull
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    @NonNull
    public String getJwksUri() {
        return jwksUri;
    }

    @NonNull
    public List<String> getResponseTypesSupported() {
        return responseTypesSupported;
    }

    @NonNull
    public List<String> getSubjectTypesSupported() {
        return subjectTypesSupported;
    }

    @NonNull
    public List<String> getIdTokenSigningAlgValuesSupported() {
        return idTokenSigningAlgValuesSupported;
    }

    @Override
    public String toString() {
        return "OpenIdDiscoveryDocument{" +
               "issuer='" + issuer + '\'' +
               ", authorizationEndpoint='" + authorizationEndpoint + '\'' +
               ", tokenEndpoint='" + tokenEndpoint + '\'' +
               ", jwksUri='" + jwksUri + '\'' +
               ", responseTypesSupported=" + responseTypesSupported +
               ", subjectTypesSupported=" + subjectTypesSupported +
               ", idTokenSigningAlgValuesSupported=" + idTokenSigningAlgValuesSupported +
               '}';
    }

    public static final class Builder {
        private String issuer;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private String jwksUri;
        private List<String> responseTypesSupported;
        private List<String> subjectTypesSupported;
        private List<String> idTokenSigningAlgValuesSupported;

        public Builder() {}

        public Builder issuer(final String val) {
            issuer = val;
            return this;
        }

        public Builder authorizationEndpoint(final String val) {
            authorizationEndpoint = val;
            return this;
        }

        public Builder tokenEndpoint(final String val) {
            tokenEndpoint = val;
            return this;
        }

        public Builder jwksUri(final String val) {
            jwksUri = val;
            return this;
        }

        public Builder responseTypesSupported(final List<String> val) {
            responseTypesSupported = val;
            return this;
        }

        public Builder subjectTypesSupported(final List<String> val) {
            subjectTypesSupported = val;
            return this;
        }

        public Builder idTokenSigningAlgValuesSupported(final List<String> val) {
            idTokenSigningAlgValuesSupported = val;
            return this;
        }

        public OpenIdDiscoveryDocument build() {
            return new OpenIdDiscoveryDocument(this);
        }
    }
}
