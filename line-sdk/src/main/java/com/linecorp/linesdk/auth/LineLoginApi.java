package com.linecorp.linesdk.auth;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.auth.internal.LineAuthenticationActivity;
import com.linecorp.linesdk.internal.EncryptorHolder;

/**
 * Represents the API that performs LINE Login.
 * <p>
 * Usage<br>
 * 1. Start login.
 * <pre>
 * Intent loginIntent = LineLoginApi.getLoginIntent(context, channelId);
 * startActivityForResult(loginIntent, REQUEST_CODE_LINE_LOGIN);
 * </pre>
 * 2. Handle the login result.
 * <pre>
 * public void onActivityResult(int requestCode, int resultCode, Intent data) {
 *     super.onActivityResult(requestCode, resultCode, data);
 *     if (requestCode != REQUEST_CODE_LINE_LOGIN) {
 *         return;
 *     }
 *     LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
 *     if (result.isSuccess()) {
 *         // You can retrieve the LINE account information and the access token
 *         // from LineLoginResult.
 *     } else {
 *         updateErrorUi();
 *     }
 * }
 * </pre>
 */
public class LineLoginApi {
    private LineLoginApi() {
        // To prevent instantiation
    }

    /**
     * Gets an intent for performing LINE Login. If LINE is installed, the SDK
     * logs in using app-to-app authentication through LINE. If LINE is not
     * installed, the SDK uses the browser to log in.
     * @param context The Android context.
     * @param channelId The channel ID.
     * @param params LINE authentication related parameters.
     * @return A login intent that defaults to using app-to-app authentication through LINE.
     */
    @NonNull
    public static Intent getLoginIntent(
            @NonNull Context context,
            @NonNull String channelId,
            @NonNull LineAuthenticationParams params) {
        return getLoginIntent(
                context,
                new LineAuthenticationConfig.Builder(channelId, context).build(),
                params);
    }

    /**
     * Gets a login intent that only performs browser login.
     * @param context The Android context.
     * @param channelId The channel ID.
     * @param params LINE authentication related parameters.
     * @return A login intent that only performs browser login.
     */
    @NonNull
    public static Intent getLoginIntentWithoutLineAppAuth(
            @NonNull Context context,
            @NonNull String channelId,
            @NonNull LineAuthenticationParams params) {
        return getLoginIntent(
                context,
                new LineAuthenticationConfig.Builder(channelId, context)
                        .disableLineAppAuthentication()
                        .build(),
                params);
    }

    /**
     * @hide
     */
    @NonNull
    public static Intent getLoginIntent(
            @NonNull Context context,
            @NonNull LineAuthenticationConfig config,
            @NonNull LineAuthenticationParams params) {
        // To minimize thread blocking time by the secret key generation.
        if (!config.isEncryptorPreparationDisabled()) {
            EncryptorHolder.initializeOnWorkerThread(context);
        }
        return LineAuthenticationActivity.getLoginIntent(context, config, params);
    }

    /**
     * Gets a {@link LineLoginResult} object from an Intent object.
     * @param intent The intent.
     * @return A {@link LineLoginResult} object.
     */
    @NonNull
    public static LineLoginResult getLoginResultFromIntent(@Nullable final Intent intent) {
        return intent == null
               ? LineLoginResult.internalError("Callback intent is null")
               : LineAuthenticationActivity.getResultFromIntent(intent);
    }
}
