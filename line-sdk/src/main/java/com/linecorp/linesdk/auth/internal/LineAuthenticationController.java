package com.linecorp.linesdk.auth.internal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationConfig;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.linecorp.linesdk.internal.AccessTokenCache;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.IssueAccessTokenResult;
import com.linecorp.linesdk.internal.OneTimePassword;
import com.linecorp.linesdk.internal.OpenIdDiscoveryDocument;
import com.linecorp.linesdk.internal.nwclient.IdTokenValidator;
import com.linecorp.linesdk.internal.nwclient.IdTokenValidator.Builder;
import com.linecorp.linesdk.internal.nwclient.LineAuthenticationApiClient;
import com.linecorp.linesdk.internal.nwclient.TalkApiClient;

import java.util.List;

/**
 * This class controls LINE authentication flow.
 */
/* package */ class LineAuthenticationController {
    private static final long CANCEL_DELAY_MILLIS = 1000;
    private static final int REQUEST_CODE = 3;

    @NonNull
    private final LineAuthenticationActivity activity;
    @NonNull
    private final LineAuthenticationConfig config;
    @NonNull
    private final LineAuthenticationApiClient authApiClient;
    @NonNull
    private final TalkApiClient talkApiClient;
    @NonNull
    private final BrowserAuthenticationApi browserAuthenticationApi;
    @NonNull
    private final AccessTokenCache accessTokenCache;
    @NonNull
    private final LineAuthenticationParams params;

    @NonNull
    private final LineAuthenticationStatus authenticationStatus;

    @Nullable
    private static Intent intentResultFromLineAPP;

    LineAuthenticationController(
            @NonNull LineAuthenticationActivity activity,
            @NonNull LineAuthenticationConfig config,
            @NonNull LineAuthenticationStatus authenticationStatus,
            @NonNull LineAuthenticationParams params) {
        this(activity,
                config,
                new LineAuthenticationApiClient(
                        activity.getApplicationContext(),
                        config.getOpenidDiscoveryDocumentUrl(),
                        config.getApiBaseUrl()),
                new TalkApiClient(
                        activity.getApplicationContext(),
                        config.getApiBaseUrl()),
                new BrowserAuthenticationApi(authenticationStatus),
                new AccessTokenCache(activity.getApplicationContext(), config.getChannelId()),
                authenticationStatus,
             params);
    }

    @VisibleForTesting
    LineAuthenticationController(
            @NonNull LineAuthenticationActivity activity,
            @NonNull LineAuthenticationConfig config,
            @NonNull LineAuthenticationApiClient authApiClient,
            @NonNull TalkApiClient talkApiClient,
            @NonNull BrowserAuthenticationApi browserAuthenticationApi,
            @NonNull AccessTokenCache accessTokenCache,
            @NonNull LineAuthenticationStatus authenticationStatus,
            @NonNull LineAuthenticationParams params) {
        this.activity = activity;
        this.config = config;
        this.authApiClient = authApiClient;
        this.talkApiClient = talkApiClient;
        this.browserAuthenticationApi = browserAuthenticationApi;
        this.accessTokenCache = accessTokenCache;
        this.authenticationStatus = authenticationStatus;
        this.params = params;
    }

    @MainThread
    void startLineAuthentication() {
        authenticationStatus.authenticationStarted();
        new RequestTokenRequestTask().execute();
    }

    private class RequestTokenRequestTask
            extends AsyncTask<Void, Void, LineApiResponse<OneTimePassword>> {

        @SuppressWarnings("OverloadedVarargsMethod")
        @Override
        protected LineApiResponse<OneTimePassword> doInBackground(
                @Nullable Void... params) {
            return authApiClient.getOneTimeIdAndPassword(config.getChannelId());
        }

        @Override
        protected void onPostExecute(
                @NonNull LineApiResponse<OneTimePassword> response) {
            if (!response.isSuccess()) {
                authenticationStatus.authenticationIntentHandled();
                activity.onAuthenticationFinished(LineLoginResult.error(response));
                return;
            }
            OneTimePassword oneTimePassword = response.getResponseData();
            authenticationStatus.setOneTimePassword(oneTimePassword);
            try {
                BrowserAuthenticationApi.Request request = browserAuthenticationApi
                        .getRequest(activity, config, oneTimePassword, params);
                if (request.isLineAppAuthentication()) {
                    // "launchMode" of the activity launched by the follows is "singleInstance".
                    // So, we must not use startActivityForResult.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activity.startActivity(
                                request.getIntent(),
                                request.getStartActivityOptions());
                    } else {
                        activity.startActivity(request.getIntent());
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activity.startActivityForResult(
                                request.getIntent(),
                                REQUEST_CODE,
                                request.getStartActivityOptions());
                    } else {
                        activity.startActivityForResult(
                                request.getIntent(),
                                REQUEST_CODE);
                    }
                }
                authenticationStatus.setSentRedirectUri(request.getRedirectUri());
            } catch (ActivityNotFoundException e) {
                authenticationStatus.authenticationIntentHandled();
                activity.onAuthenticationFinished(LineLoginResult.internalError(e));
            }
        }
    }

    @MainThread
    public static void setIntent(Intent intent) {
        intentResultFromLineAPP = intent;
    }

    @MainThread
    void handleIntentFromLineApp(@NonNull Intent intent) {
        authenticationStatus.authenticationIntentReceived();
        BrowserAuthenticationApi.Result authResult =
                browserAuthenticationApi.getAuthenticationResultFrom(intent);
        if (!authResult.isSuccess()) {
            authenticationStatus.authenticationIntentHandled();
            final LineLoginResult errorResult =
                    authResult.isAuthenticationAgentError()
                    ? LineLoginResult.authenticationAgentError(authResult.getLineApiError())
                    : LineLoginResult.internalError(authResult.getLineApiError());
            activity.onAuthenticationFinished(errorResult);
            return;
        }
        new AccessTokenRequestTask().execute(authResult);
    }

    @MainThread
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != REQUEST_CODE
                || authenticationStatus.getStatus() == LineAuthenticationStatus.Status.INTENT_RECEIVED) {
            return;
        }
        // onActivityResult may be called before calling onNewIntent.
        // To wait the calling onNewIntent, this cancels the authentication with a delay.
        new Handler(Looper.getMainLooper())
                .postDelayed(new CancelAuthenticationTask(), CANCEL_DELAY_MILLIS);
    }

    @MainThread
    void handleCancel() {
        // 1. with passcode in LINE app, when user presses back on passcode screen, onStart will be called without onActivityResult, nor onNewIntent
        // 2. if passcode is correct, it takes more time to get onNewIntent
        // onStart is called before calling onNewIntent, we need to wait a delay to make sure onStart is really for cancel.
        new Handler(Looper.getMainLooper())
                .postDelayed(new CancelAuthenticationTask(), CANCEL_DELAY_MILLIS);
    }

    private class CancelAuthenticationTask implements Runnable {
        @MainThread
        @Override
        public void run() {
            if (authenticationStatus.getStatus() == LineAuthenticationStatus.Status.INTENT_RECEIVED
                    || activity.isFinishing()) {
                return;
            }
            // before returning "cancel" result to app, check whether intent is already
            // returned from LINE app. If so, handle it.
            if (intentResultFromLineAPP != null) {
                handleIntentFromLineApp(intentResultFromLineAPP);
                intentResultFromLineAPP = null;
                return;
            }

            activity.onAuthenticationFinished(LineLoginResult.canceledError());
        }
    }

    private class AccessTokenRequestTask extends AsyncTask<BrowserAuthenticationApi.Result, Void, LineLoginResult> {

        @SuppressWarnings("OverloadedVarargsMethod")
        @Override
        protected LineLoginResult doInBackground(@Nullable BrowserAuthenticationApi.Result... params) {
            BrowserAuthenticationApi.Result authResult = params[0];
            String requestToken = authResult.getRequestToken();
            OneTimePassword oneTimePassword = authenticationStatus.getOneTimePassword();
            String sentRedirectUri = authenticationStatus.getSentRedirectUri();
            if (TextUtils.isEmpty(requestToken)
                    || oneTimePassword == null
                    || TextUtils.isEmpty(sentRedirectUri)) {
                return LineLoginResult.internalError("Requested data is missing.");
            }

            // Acquire access token
            LineApiResponse<IssueAccessTokenResult> accessTokenResponse =
                    authApiClient.issueAccessToken(
                            config.getChannelId(), requestToken, oneTimePassword, sentRedirectUri);
            if (!accessTokenResponse.isSuccess()) {
                return LineLoginResult.error(accessTokenResponse);
            }

            IssueAccessTokenResult issueAccessTokenResult = accessTokenResponse.getResponseData();
            InternalAccessToken accessToken = issueAccessTokenResult.getAccessToken();
            List<Scope> scopes = issueAccessTokenResult.getScopes();

            LineProfile lineProfile = null;
            String userId = null;
            if (scopes.contains(Scope.PROFILE)) {
                // Acquire account information
                LineApiResponse<LineProfile> profileResponse = talkApiClient.getProfile(accessToken);
                if (!profileResponse.isSuccess()) {
                    return LineLoginResult.error(profileResponse);
                }
                lineProfile = profileResponse.getResponseData();
                userId = lineProfile.getUserId();
            }

            // Cache the acquired access token
            accessTokenCache.saveAccessToken(accessToken);

            final LineIdToken idToken = issueAccessTokenResult.getIdToken();
            if (idToken != null) {
                try {
                    validateIdToken(idToken, userId);
                } catch (final Exception e) {
                    return LineLoginResult.internalError(e.getMessage());
                }
            }

            return new LineLoginResult.Builder()
                    .nonce(authenticationStatus.getOpenIdNonce())
                    .lineProfile(lineProfile)
                    .lineIdToken(idToken)
                    .friendshipStatusChanged(authResult.getFriendshipStatusChanged())
                    .lineCredential(new LineCredential(
                            new LineAccessToken(
                                    accessToken.getAccessToken(),
                                    accessToken.getExpiresInMillis(),
                                    accessToken.getIssuedClientTimeMillis()),
                            scopes
                    ))
                    .build();
        }

        private void validateIdToken(final LineIdToken idToken, final String userId) {
            final LineApiResponse<OpenIdDiscoveryDocument> response =
                    authApiClient.getOpenIdDiscoveryDocument();
            if (!response.isSuccess()) {
                throw new RuntimeException("Failed to get OpenId Discovery Document. "
                                           + " Response Code: " + response.getResponseCode()
                                           + " Error Data: " + response.getErrorData());
            }

            final OpenIdDiscoveryDocument openIdDiscoveryDoc = response.getResponseData();

            final IdTokenValidator idTokenValidator = new Builder()
                    .idToken(idToken)
                    .expectedIssuer(openIdDiscoveryDoc.getIssuer())
                    .expectedUserId(userId)
                    .expectedChannelId(config.getChannelId())
                    .expectedNonce(authenticationStatus.getOpenIdNonce())
                    .build();

            idTokenValidator.validate();
        }

        @Override
        protected void onPostExecute(@NonNull LineLoginResult lineLoginResult) {
            authenticationStatus.authenticationIntentHandled();
            activity.onAuthenticationFinished(lineLoginResult);
        }
    }
}
