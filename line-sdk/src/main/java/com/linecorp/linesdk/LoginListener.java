package com.linecorp.linesdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.auth.LineLoginResult;

/**
 * Represents a listener for the login result. The {@link #onLoginSuccess(LineLoginResult)} method
 * is called if the login is successful; the {@link #onLoginFailure(LineLoginResult)} is called
 * otherwise.
 *
 * @see com.linecorp.linesdk.widget.LoginButton
 */
public interface LoginListener {
    /**
     * Called by {@link com.linecorp.linesdk.internal.LoginHandler} if the login succeeds.
     *
     * @param result The login result for a successful login. It contains information about the login.
     * */
    void onLoginSuccess(@NonNull LineLoginResult result);

    /**
     * Called by {@link com.linecorp.linesdk.internal.LoginHandler} if the login fails.
     *
     * @param result The login result for a failed login. It contains error information that can be used to diagnose the failure. <code>Null</code> if
     *               the login is cancelled.
     * */
    void onLoginFailure(@Nullable LineLoginResult result);
}
