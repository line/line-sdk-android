package com.linecorp.linesdk.internal;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LoginListener;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

import java.util.ArrayList;

/**
 * A handler to perform login through
 * {@link #performLogin(Activity, boolean, String, LineAuthenticationParams)} or
 * {@link #performLogin(Activity, FragmentWrapper, boolean, String, LineAuthenticationParams)} and
 * handle the login result through {@link #onActivityResult(int, int, Intent)}.
 * <p>
 * If you perform login from an <code>Activity</code>, calls
 * {@link #performLogin(Activity, boolean, String, LineAuthenticationParams)}; if you perform login
 * from a <code>Fragment</code>, calls
 * {@link #performLogin(Activity, FragmentWrapper, boolean, String, LineAuthenticationParams)}.
 */
public class LoginHandler {
    private static String TAG = "LoginHandler";
    private static int REQUEST_CODE_LOGIN = 1;

    @NonNull
    private ArrayList<LoginListener> loginListeners = new ArrayList<>();

    public void performLogin(
            @NonNull Activity activity,
            boolean isLineAppAuthEnabled,
            @NonNull String channelId,
            @NonNull LineAuthenticationParams params
    ) {
        Intent intent = getLoginIntent(activity, isLineAppAuthEnabled, channelId, params);
        activity.startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    public void performLogin(
            @NonNull Activity activity,
            @NonNull FragmentWrapper fragmentWrapper,
            boolean isLineAppAuthEnabled,
            @NonNull String channelId,
            @NonNull LineAuthenticationParams params
    ) {
        Intent intent = getLoginIntent(activity, isLineAppAuthEnabled, channelId, params);
        fragmentWrapper.startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @NonNull
    private Intent getLoginIntent(
            @NonNull Activity activity,
            boolean isLineAppAuthEnabled,
            @NonNull String channelId,
            @NonNull LineAuthenticationParams params
    ) {
        Intent intent;
        if (isLineAppAuthEnabled) {
            intent = LineLoginApi.getLoginIntent(activity, channelId, params);
        } else {
            intent = LineLoginApi.getLoginIntentWithoutLineAppAuth(activity, channelId, params);
        }
        return intent;
    }

    boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!isLoginRequestCode(requestCode)) {
            Log.w(TAG, "Unexpected login request code");
            return false;
        }

        if (isLoginCanceled(resultCode, data)) {
            Log.w(TAG, "Login failed");
            return false;
        }

        LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
        if (isLoginSuccess(result)) {
            onLoginSuccess(result);
        } else {
            onLoginFailure(result);
        }
        return true;
    }

    private boolean isLoginSuccess(@Nullable LineLoginResult result) {
        return result != null && result.getResponseCode() == LineApiResponseCode.SUCCESS;
    }

    private boolean isLoginCanceled(int resultCode, Intent data) {
        return resultCode != Activity.RESULT_OK || data == null;
    }

    private boolean isLoginRequestCode(int requestCode) {
        return requestCode == REQUEST_CODE_LOGIN;
    }

    private void onLoginFailure(@Nullable LineLoginResult result) {
        for (LoginListener loginListener : loginListeners) {
            loginListener.onLoginFailure(result);
        }
    }

    private void onLoginSuccess(LineLoginResult result) {
        for (LoginListener loginListener : loginListeners) {
            loginListener.onLoginSuccess(result);
        }
    }

    public void addLoginListener(@NonNull LoginListener loginListener) {
        loginListeners.add(loginListener);
    }

    public void removeLoginListener(@NonNull LoginListener loginListener) {
        loginListeners.remove(loginListener);
    }
}
