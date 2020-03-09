package com.linecorp.linesdk.internal.nwclient;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.internal.AccessTokenVerificationResult;
import com.linecorp.linesdk.internal.IdTokenKeyType;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.IssueAccessTokenResult;
import com.linecorp.linesdk.internal.JWKSet;
import com.linecorp.linesdk.internal.OpenIdDiscoveryDocument;
import com.linecorp.linesdk.internal.RefreshTokenResult;
import com.linecorp.linesdk.internal.nwclient.core.ChannelServiceHttpClient;
import com.linecorp.linesdk.internal.nwclient.core.ResponseDataParser;
import com.linecorp.linesdk.internal.pkce.PKCECode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import static com.linecorp.linesdk.utils.UriUtils.buildParams;
import static com.linecorp.linesdk.utils.UriUtils.buildUri;
import static java.util.Collections.emptyMap;

/**
 * Internal LINE OAUTH API client to process internal process such as building request data and
 * parsing response data.
 */
public class LineAuthenticationApiClient {
    private static final String TAG = "LineAuthApiClient";

    private static final String BASE_PATH_OAUTH_V21_API = "oauth2/v2.1";
    private static final String AVAILABLE_TOKEN_TYPE = "Bearer";

    @NonNull
    private final Uri apiBaseUrl;

    @NonNull
    private final ChannelServiceHttpClient httpClient;

    private final ResponseDataParser<IssueAccessTokenResult> ISSUE_ACCESS_TOKEN_RESULT_PARSER =
            this.new IssueAccessTokenResultParser();
    private static final ResponseDataParser<AccessTokenVerificationResult> VERIFICATION_RESULT_PARSER =
            new VerificationResultParser();
    private static final ResponseDataParser<RefreshTokenResult> REFRESH_TOKEN_RESULT_PARSER =
            new RefreshTokenResultParser();
    private static final ResponseDataParser<?> NO_RESULT_RESPONSE_PARSER =
            new NoResultResponseParser();
    private static final ResponseDataParser<OpenIdDiscoveryDocument> OPEN_ID_DISCOVERY_DOCUMENT_PARSER =
            new OpenIdDiscoveryDocumentParser();
    private static final ResponseDataParser<JWKSet> JWK_SET_PARSER =
            new JWKSetParser();
    private final OpenIdSigningKeyResolver signingKeyResolver = new OpenIdSigningKeyResolver(this);

    @NonNull
    private final Uri openidDiscoveryDocumentUrl;

    public LineAuthenticationApiClient(@NonNull final Context applicationContext,
                                       @NonNull final Uri openidDiscoveryDocumentUrl,
                                       @NonNull final Uri apiBaseUrl) {
        this(openidDiscoveryDocumentUrl,
                apiBaseUrl,
                new ChannelServiceHttpClient(applicationContext, BuildConfig.VERSION_NAME));
    }

    @VisibleForTesting
    LineAuthenticationApiClient(
            @NonNull final Uri openidDiscoveryDocumentUrl,
            @NonNull final Uri apiBaseUrl,
            @NonNull final ChannelServiceHttpClient httpClient) {
        this.apiBaseUrl = apiBaseUrl;
        this.httpClient = httpClient;
        this.openidDiscoveryDocumentUrl = openidDiscoveryDocumentUrl;
    }

    @NonNull
    public LineApiResponse<IssueAccessTokenResult> issueAccessToken(
            @NonNull String channelId,
            @NonNull String requestToken,
            @NonNull PKCECode pkceCode,
            @NonNull String redirectUri) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OAUTH_V21_API, "token");
        final Map<String, String> postData = buildParams(
                "grant_type", "authorization_code",
                "code", requestToken,
                "redirect_uri", redirectUri,
                "client_id", channelId,
                "code_verifier", pkceCode.getVerifier(),
                "id_token_key_type", IdTokenKeyType.JWK.name(),
                "client_version", "LINE SDK Android v" + BuildConfig.VERSION_NAME
        );
        return httpClient.post(
                uri,
                emptyMap() /* requestHeaders */,
                postData,
                ISSUE_ACCESS_TOKEN_RESULT_PARSER);
    }

    private class IssueAccessTokenResultParser
            extends JsonToObjectBaseResponseParser<IssueAccessTokenResult> {
        @NonNull
        @Override
        protected IssueAccessTokenResult parseJsonToObject(
                @NonNull JSONObject jsonObject) throws JSONException {
            String tokenType = jsonObject.getString("token_type");
            if (!AVAILABLE_TOKEN_TYPE.equals(tokenType)) {
                throw new JSONException("Illegal token type. token_type=" + tokenType);
            }

            final InternalAccessToken accessToken = new InternalAccessToken(
                    jsonObject.getString("access_token"),
                    jsonObject.getLong("expires_in") * 1000,
                    System.currentTimeMillis() /* issuedClientTimeMillis */,
                    jsonObject.getString("refresh_token"));

            final List<Scope> scopes = Scope.parseToList(jsonObject.getString("scope"));

            final LineIdToken idToken;
            try {
                idToken = parseIdToken(jsonObject.optString("id_token"));
            } catch (final Exception e) {
                throw new JSONException(e.getMessage());
            }

            return new IssueAccessTokenResult(accessToken, scopes, idToken);
        }

        private LineIdToken parseIdToken(final String idTokenStr) throws Exception {
            if (TextUtils.isEmpty(idTokenStr)) {
                return null;
            }

            return IdTokenParser.parse(idTokenStr, signingKeyResolver);
        }
    }

    @NonNull
    public LineApiResponse<AccessTokenVerificationResult> verifyAccessToken(
            @NonNull InternalAccessToken accessToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OAUTH_V21_API, "verify");
        final Map<String, String> queryParams = buildParams(
                "access_token", accessToken.getAccessToken()
        );
        return httpClient.get(
                uri,
                emptyMap() /* requestHeaders */,
                queryParams,
                VERIFICATION_RESULT_PARSER);
    }

    private static class VerificationResultParser
            extends JsonToObjectBaseResponseParser<AccessTokenVerificationResult> {
        @NonNull
        @Override
        protected AccessTokenVerificationResult parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return new AccessTokenVerificationResult(
                    jsonObject.getString("client_id"),
                    jsonObject.getLong("expires_in") * 1000,
                    Scope.parseToList(jsonObject.getString("scope")));
        }
    }

    @NonNull
    public LineApiResponse<RefreshTokenResult> refreshToken(
            @NonNull String channelId, @NonNull InternalAccessToken accessToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OAUTH_V21_API, "token");
        final Map<String, String> postData = buildParams(
                "grant_type", "refresh_token",
                "refresh_token", accessToken.getRefreshToken(),
                "client_id", channelId
        );
        return httpClient.post(
                uri,
                emptyMap() /* requestHeaders */,
                postData,
                REFRESH_TOKEN_RESULT_PARSER);
    }

    private static class RefreshTokenResultParser
            extends JsonToObjectBaseResponseParser<RefreshTokenResult> {
        @NonNull
        @Override
        protected RefreshTokenResult parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            String tokenType = jsonObject.getString("token_type");
            if (!AVAILABLE_TOKEN_TYPE.equals(tokenType)) {
                throw new JSONException("Illegal token type. token_type=" + tokenType);
            }
            return new RefreshTokenResult(
                    jsonObject.getString("access_token"),
                    jsonObject.getLong("expires_in") * 1000,
                    jsonObject.getString("refresh_token"),
                    Scope.parseToList(jsonObject.getString("scope")));
        }
    }

    @NonNull
    public LineApiResponse<?> revokeAccessToken(@NonNull String channelId,
                                                @NonNull InternalAccessToken accessToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OAUTH_V21_API, "revoke");
        final Map<String, String> postData = buildParams(
                "access_token", accessToken.getAccessToken(),
                "client_id", channelId
        );
        return httpClient.post(
                uri,
                emptyMap() /* requestHeaders */,
                postData,
                NO_RESULT_RESPONSE_PARSER);
    }

    @NonNull
    public LineApiResponse<?> revokeRefreshToken(@NonNull String channelId,
                                                 @NonNull InternalAccessToken accessToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OAUTH_V21_API, "revoke");
        final Map<String, String> postData = buildParams(
                "refresh_token", accessToken.getRefreshToken(),
                "client_id", channelId
        );
        return httpClient.post(
                uri,
                emptyMap() /* requestHeaders */,
                postData,
                NO_RESULT_RESPONSE_PARSER);
    }

    @NonNull
    public LineApiResponse<OpenIdDiscoveryDocument> getOpenIdDiscoveryDocument() {
        final Uri uri = buildUri(openidDiscoveryDocumentUrl);
        final LineApiResponse<OpenIdDiscoveryDocument> response =
                httpClient.get(uri,
                        emptyMap(),
                        emptyMap(),
                        OPEN_ID_DISCOVERY_DOCUMENT_PARSER);

        if (!response.isSuccess()) {
            Log.e(TAG, "getOpenIdDiscoveryDocument failed: " + response);
        }

        return response;
    }

    @NonNull
    public LineApiResponse<JWKSet> getJWKSet() {
        final LineApiResponse<OpenIdDiscoveryDocument> discoveryDocResponse = getOpenIdDiscoveryDocument();

        if (!discoveryDocResponse.isSuccess()) {
            return LineApiResponse.createAsError(discoveryDocResponse.getResponseCode(),
                    discoveryDocResponse.getErrorData());
        }

        final OpenIdDiscoveryDocument openIdDiscoveryDoc = discoveryDocResponse.getResponseData();
        final Uri jwksUri = Uri.parse(openIdDiscoveryDoc.getJwksUri());

        final LineApiResponse<JWKSet> jwkSetResponse =
                httpClient.get(jwksUri,
                        emptyMap(),
                        emptyMap(),
                        JWK_SET_PARSER);
        if (!jwkSetResponse.isSuccess()) {
            Log.e(TAG, "getJWKSet failed: " + jwkSetResponse);
        }

        return jwkSetResponse;
    }
}
