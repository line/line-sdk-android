package com.linecorp.linesdk.auth.internal;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.Constants;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationConfig;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.internal.pkce.CodeChallengeMethod;
import com.linecorp.linesdk.internal.pkce.PKCECode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import static com.linecorp.linesdk.utils.StringUtils.createRandomAlphaNumeric;
import static com.linecorp.linesdk.utils.UriUtils.appendQueryParams;
import static com.linecorp.linesdk.utils.UriUtils.buildParams;

/**
 * Class represents the LINE authentication API for browser log-in.
 */
/* package */ class BrowserAuthenticationApi {
    private static final int LENGTH_OAUTH_STATE = 16;

    private static final int LENGTH_OPENID_NONCE = 16;

    static class Request {
        @NonNull
        private final Intent intent;
        @Nullable
        private final Bundle startActivityOptions;
        @NonNull
        private final String redirectUri;
        private final boolean isLineAppAuthentication;

        @VisibleForTesting
        Request(@NonNull Intent intent,
                @Nullable Bundle startActivityOptions,
                @NonNull String redirectUri,
                boolean isLineAppAuthentication) {
            this.intent = intent;
            this.startActivityOptions = startActivityOptions;
            this.redirectUri = redirectUri;
            this.isLineAppAuthentication = isLineAppAuthentication;
        }

        @NonNull
        Intent getIntent() {
            return intent;
        }

        @Nullable
        Bundle getStartActivityOptions() {
            return startActivityOptions;
        }

        @NonNull
        String getRedirectUri() {
            return redirectUri;
        }

        boolean isLineAppAuthentication() {
            return isLineAppAuthentication;
        }
    }

    @NonNull
    private final LineAuthenticationStatus authenticationStatus;

    BrowserAuthenticationApi(@NonNull LineAuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    @NonNull
    Request getRequest(
            @NonNull Context context,
            @NonNull LineAuthenticationConfig config,
            @NonNull PKCECode pkceCode,
            @NonNull LineAuthenticationParams params)
            throws ActivityNotFoundException {

        // "state" may be guessed easily but there is no problem as the follows.
        // In case of LINE SDK, the correctness of "redirect_uri" will be checked with using PKCE
        // instead of "state".
        final String oAuthState = createRandomAlphaNumeric(LENGTH_OAUTH_STATE);
        authenticationStatus.setOAuthState(oAuthState);

        final String openIdNonce;
        if (params.getScopes().contains(Scope.OPENID_CONNECT)) {
            if (!TextUtils.isEmpty(params.getNonce())) {
                openIdNonce = params.getNonce();
            } else {
                // generate a random string for it, if no `nonce` param specified
                openIdNonce = createRandomAlphaNumeric(LENGTH_OPENID_NONCE);
            }
        } else {
            openIdNonce = null;
        }
        authenticationStatus.setOpenIdNonce(openIdNonce);

        final String redirectUri = createRedirectUri(context);

        final Uri loginUri = createLoginUrl(config, pkceCode, params, oAuthState, openIdNonce, redirectUri);

        AuthenticationIntentHolder intentHolder = getAuthenticationIntentHolder(
                context, loginUri, config.isLineAppAuthenticationDisabled());

        return new Request(
                intentHolder.getIntent(),
                intentHolder.getStartActivityOptions(),
                redirectUri,
                intentHolder.isLineAppAuthentication);
    }

    @VisibleForTesting
    boolean isChromeCustomTabSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    @VisibleForTesting
    @NonNull
    Uri createLoginUrl(
            @NonNull LineAuthenticationConfig config,
            @NonNull PKCECode pkceCode,
            @NonNull LineAuthenticationParams params,
            @NonNull String oAuthState,
            @Nullable String openIdNonce,
            @NonNull String redirectUri) {
        final String baseReturnUri = "/oauth2/v2.1/authorize/consent";
        final Map<String, String> returnQueryParams = buildParams(
                "response_type", "code",
                "client_id", config.getChannelId(),
                "state", oAuthState,
                "code_challenge", pkceCode.getChallenge(),
                "code_challenge_method", CodeChallengeMethod.S256.getValue(),
                "redirect_uri", redirectUri,
                "sdk_ver", BuildConfig.VERSION_NAME,
                "scope", Scope.join(params.getScopes())
        );
        if (!TextUtils.isEmpty(openIdNonce)) {
            returnQueryParams.put("nonce", openIdNonce);
        }
        if (params.getBotPrompt() != null) {
            returnQueryParams.put("bot_prompt", params.getBotPrompt().name().toLowerCase());
        }

        final String returnUri = appendQueryParams(baseReturnUri, returnQueryParams)
                .toString();

        final Map<String, String> loginQueryParams = buildParams(
                "returnUri", returnUri,
                "loginChannelId", config.getChannelId()
        );
        if (params.getUILocale() != null) {
            loginQueryParams.put("ui_locales", params.getUILocale().toString());
        }
        return appendQueryParams(config.getWebLoginPageUrl(), loginQueryParams);
    }

    @VisibleForTesting
    @NonNull
    String createRedirectUri(@NonNull Context context) {
        // A host name must be set even if it is not used because a browser regards as an opaque uri.
        // If a browser redirects with an opaque uri, we can not parse query parameters by using
        // Uri class.
        return "intent://result#Intent;package=" + context.getPackageName() + ";scheme=lineauth;end";
    }

    /**
     * Returns {@link AuthenticationIntentHolder} that holds information to start authentication.
     * If the following conditions are satisfied, this returns an intent to launch LINE
     * application. Otherwise returns an intent to launch an application other than LINE
     * application such as browser.
     * - LINE application is installed.
     * - LINE application version is 6.9.0 or more.
     * (LINE auto login feature is available before 6.9.0 but "scope" and "otpId" are not available
     * on the versions.)
     * - The method parameter value of 'isLineAppAuthDisabled' is false.
     * If the above conditions are satisfied, the return intent launches LINE application even if
     * the current user sets an external browser as the auto login page launching setting.
     */
    @VisibleForTesting
    @NonNull
    AuthenticationIntentHolder getAuthenticationIntentHolder(
            @NonNull Context context, @NonNull Uri loginUri, boolean isLineAppAuthDisabled)
            throws ActivityNotFoundException {

        Intent intent;
        Bundle startActivityOptions;
        if (isChromeCustomTabSupported()) {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(context, android.R.color.white))
                    .build();
            intent = customTabsIntent.intent.setData(loginUri);
            startActivityOptions = customTabsIntent.startAnimationBundle;
        } else {
            intent = new Intent(Intent.ACTION_VIEW).setData(loginUri);
            startActivityOptions = null;
        }

        LineAppVersion lineAppVersion = LineAppVersion.getLineAppVersion(context);
        boolean shouldLaunchLineApp = !isLineAppAuthDisabled;
        if (shouldLaunchLineApp && lineAppVersion != null) {
            Intent lineAppIntent = new Intent(Intent.ACTION_VIEW);
            lineAppIntent.setData(loginUri);
            lineAppIntent.setPackage(Constants.LINE_APP_PACKAGE_NAME);

            if (resolveActivity(context, lineAppIntent) != null) {
                return new AuthenticationIntentHolder(lineAppIntent, startActivityOptions, true /* isLineAppAuthentication */);
            }
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"));
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(browserIntent, 0 /* flags */);
        List<Intent> targetIntents = convertToIntents(loginUri, resolveInfoList, intent.getExtras());

        int targetIntentCount = targetIntents.size();
        if (targetIntentCount == 0) {
            throw new ActivityNotFoundException("Activity for LINE log-in is not found. uri=" + loginUri);
        }
        if (targetIntentCount == 1) {
            return new AuthenticationIntentHolder(
                    targetIntents.get(0), startActivityOptions, false /* isLineAppAuthentication */);
        }

        Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), null /* title */);
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                targetIntents.toArray(new Parcelable[targetIntents.size()]));
        return new AuthenticationIntentHolder(
                chooserIntent, startActivityOptions, false /* isLineAppAuthentication */);
    }

    @VisibleForTesting
    ComponentName resolveActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return intent.resolveActivity(packageManager);
    }

    @NonNull
    private static List<Intent> convertToIntents(
            @NonNull Uri data,
            @NonNull Collection<ResolveInfo> resolveInfoList,
            @Nullable Bundle extras) {
        List<Intent> targetIntents = new ArrayList<>(resolveInfoList.size());
        for (ResolveInfo resolveInfo : resolveInfoList) {
            Intent targetIntent = new Intent(Intent.ACTION_VIEW);
            targetIntent.setData(data);
            targetIntent.setPackage(resolveInfo.activityInfo.packageName);
            if (extras != null) {
                targetIntent.putExtras(extras);
            }
            targetIntents.add(targetIntent);
        }
        return targetIntents;
    }

    @NonNull
    Result getAuthenticationResultFrom(@NonNull Intent resultIntent) {
        Uri resultDataUri = resultIntent.getData();
        if (resultDataUri == null) {
            return Result.createAsInternalError(
                    "Illegal redirection from external application.");
        }
        String sentState = authenticationStatus.getOAuthState();
        String receivedState = resultDataUri.getQueryParameter("state");
        if (sentState == null || !sentState.equals(receivedState)) {
            return Result.createAsInternalError(
                    "Illegal parameter value of 'state'.");
        }
        String requestToken = resultDataUri.getQueryParameter("code");
        String friendshipStatusChangedStr = resultDataUri.getQueryParameter("friendship_status_changed");
        Boolean friendshipStatusChanged = null;
        if (!TextUtils.isEmpty(friendshipStatusChangedStr)) {
            friendshipStatusChanged = Boolean.parseBoolean(friendshipStatusChangedStr);
        }

        return !TextUtils.isEmpty(requestToken)
               ? Result.createAsSuccess(requestToken, friendshipStatusChanged)
               : Result.createAsAuthenticationAgentError(
                resultDataUri.getQueryParameter("error"),
                resultDataUri.getQueryParameter("error_description"));
    }

    /* package */ static class Result {
        @Nullable
        private final String requestToken;
        @Nullable
        private final Boolean friendshipStatusChanged;
        @Nullable
        private final String serverErrorCode;
        @Nullable
        private final String serverErrorDescription;
        @Nullable
        private final String internalErrorMessage;

        private Result(
                @Nullable String requestToken,
                @Nullable Boolean friendshipStatusChanged,
                @Nullable String serverErrorCode,
                @Nullable String serverErrorDescription,
                @Nullable String internalErrorMessage) {
            this.requestToken = requestToken;
            this.friendshipStatusChanged = friendshipStatusChanged;
            this.serverErrorCode = serverErrorCode;
            this.serverErrorDescription = serverErrorDescription;
            this.internalErrorMessage = internalErrorMessage;
        }

        @VisibleForTesting
        @NonNull
        static Result createAsSuccess(@NonNull String requestToken, @Nullable Boolean friendshipStatusChanged) {
            return new Result(
                    requestToken,
                    friendshipStatusChanged,
                    null /* serverErrorCode */,
                    null /* serverErrorDescription */,
                    null /* internalErrorMessage */);
        }

        @VisibleForTesting
        @NonNull
        static Result createAsAuthenticationAgentError(
                @NonNull String error, @NonNull String errorDescription) {
            return new Result(
                    null /* requestToken */,
                    null, /* friendshipStatusChanged */
                    error /* serverErrorCode */,
                    errorDescription /* serverErrorDescription */,
                    null /* internalErrorMessage */);
        }

        @VisibleForTesting
        @NonNull
        static Result createAsInternalError(
                @NonNull String errorMessage) {
            return new Result(
                    null /* requestToken */,
                    null, /* friendshipStatusChanged */
                    null /* serverErrorCode */,
                    null /* serverErrorDescription */,
                    errorMessage);
        }

        boolean isSuccess() {
            return !TextUtils.isEmpty(requestToken);
        }

        boolean isAuthenticationAgentError() {
            return TextUtils.isEmpty(internalErrorMessage) && !isSuccess();
        }

        private void checkRequestToken() {
            if (TextUtils.isEmpty(requestToken)) {
                throw new UnsupportedOperationException(
                        "requestToken is null. Please check result by isSuccess before.");
            }
        }

        @NonNull
        String getRequestToken() {
            checkRequestToken();
            return requestToken;
        }

        @Nullable
        Boolean getFriendshipStatusChanged() {
            checkRequestToken();
            return friendshipStatusChanged;
        }

        @NonNull
        LineApiError getLineApiError() {
            if (isAuthenticationAgentError()) {
                try {
                    return new LineApiError(
                            new JSONObject()
                                    .putOpt("error", serverErrorCode)
                                    .putOpt("error_description", serverErrorDescription)
                                    .toString());
                } catch (JSONException e) {
                    return new LineApiError(e);
                }
            }
            return new LineApiError(internalErrorMessage);
        }
    }

    @VisibleForTesting
    static class AuthenticationIntentHolder {
        @NonNull
        private final Intent intent;
        @Nullable
        private final Bundle startActivityOptions;
        private final boolean isLineAppAuthentication;

        AuthenticationIntentHolder(
                @NonNull Intent intent,
                @Nullable Bundle startActivityOptions,
                boolean isLineAppAuthentication) {
            this.intent = intent;
            this.startActivityOptions = startActivityOptions;
            this.isLineAppAuthentication = isLineAppAuthentication;
        }

        @NonNull
        public Intent getIntent() {
            return intent;
        }

        @Nullable
        public Bundle getStartActivityOptions() {
            return startActivityOptions;
        }

        public boolean isLineAppAuthentication() {
            return isLineAppAuthentication;
        }
    }
}
