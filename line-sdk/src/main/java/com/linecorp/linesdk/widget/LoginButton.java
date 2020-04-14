package com.linecorp.linesdk.widget;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.linecorp.linesdk.LoginDelegate;
import com.linecorp.linesdk.LoginListener;
import com.linecorp.linesdk.R;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.internal.FragmentWrapper;
import com.linecorp.linesdk.internal.LoginDelegateImpl;
import com.linecorp.linesdk.internal.LoginHandler;

import java.util.Arrays;

/**
 * A button widget that simplifies the login flow. Before you add login listeners to this login button through the
 * {@link #addLoginListener(LoginListener)} method, set your channel ID through the
 * {@link #setChannelId(String)} method and login delegate through the
 * {@link #setLoginDelegate(LoginDelegate)} method. Otherwise, a {@link RuntimeException} is thrown.
 * Also, use the provided {@link LoginDelegate.Factory#create()} method to create a
 * {@link LoginDelegate} instance and set it to this button through the
 * {@link #setLoginDelegate(LoginDelegate)} method.
 * <p>
 * By default, this button performs the login process using LINE with the {@link Scope#PROFILE}
 * scope only. You can create your own {@link LineAuthenticationParams} instance using the provided
 * {@link LineAuthenticationParams.Builder} method and set the authentication parameters to this
 * button through the {@link #setAuthenticationParams(LineAuthenticationParams)} method. You can
 * also control whether the user logs in with LINE or with the browser through the
 * {@link #enableLineAppAuthentication(boolean)} method.
 * <p>
 * Finally, call the {@link LoginDelegate#onActivityResult(int, int, Intent)} method using the
 * intent that you created with the <code>Activity#onActivityResult(int, int, Intent)</code> method.
 * If you use this button in a <code>Fragment</code> or an <code>androidx.fragment.app.Fragment</code>
 * instance, set the fragment to this button through the {@link #setFragment(Fragment)} method or
 * the {@link #setFragment(androidx.fragment.app.Fragment)} method. By doing so, you can call the
 * <code>onActivityResult</code> callback in your fragment after the login process is complete.
 * <p>
 * The following example shows how to set up the login button with the desired parameters.
 * <pre>
 * int loginButtonResId = ...;
 * String channelId = ...;
 * LoginDelegate loginDelegate = LoginDelegate.Factory.create();
 * LineAuthenticationParams params = LineAuthenticationParams.Builder()
 *                                         .scopes(...)
 *                                         .nonce(...)
 *                                         .botPrompt(...)
 *                                         .build();
 *
 * LoginButton loginButton = findViewById(loginButtonResId);
 * loginButton.setChannelId(channelId);
 * loginButton.setLoginDelegate(loginDelegate);
 * loginButton.enableLineAppAuthentication(true);
 * loginButton.setAuthenticationParams(params);
 * loginButton.addLoginListener(new LoginListener() {
 *    {@literal @}Override
 *     public void onLoginSuccess(@NonNull LineLoginResult result) {
 *         ...
 *     }
 *
 *    {@literal @}Override
 *     public void onLoginFailure(@Nullable LineLoginResult result) {
 *     if (result != null) {
 *         ...
 *     } else {
 *         ...
 *     }
 * });
 * </pre>
 * The example below handles the login result intent in the <code>onActivityResult</code> method.
 * <pre>
 * {@literal @}Override
 * public void onActivityResult(int requestCode, int resultCode, Intent data) {
 *     super.onActivityResult(requestCode, resultCode, data);
 *     if (loginDelegate.onActivityResult(requestCode, resultCode, data)) {
 *         // login result intent is consumed.
 *         return;
 *     }
 * }
 * </pre>
 */
public class LoginButton extends AppCompatTextView {
    /**
     * The channel ID is required for the login process.
     */
    @Nullable
    private String channelId;

    /**
     * The login delegate is required for handling the result intent from the
     * <code>onActivityResult</code> method of an <code>Activity</code> instance or a
     * <code>Fragment</code> instance.
     */
    @Nullable
    private LoginDelegate loginDelegate;

    /**
     * Decides whether the user logs in with LINE or with the browser. True to let the user log in
     * with LINE; false otherwise.
     */
    private boolean isLineAppAuthEnabled = true;

    /**
     * The authentication parameters for the login process. The default value allows the
     * {@link Scope#PROFILE} scope only.
     */
    @NonNull
    private LineAuthenticationParams authenticationParams = new LineAuthenticationParams.Builder()
            .scopes(Arrays.asList(Scope.PROFILE))
            .build();

    @Nullable
    private FragmentWrapper fragmentWrapper;

    @NonNull
    private LoginHandler loginHandler = new LoginHandler();

    @NonNull
    private OnClickListener internalListener = view -> {
        if (channelId == null) {
            throw new RuntimeException("Channel id should be set.");
        }

        if (channelId.isEmpty()) {
            throw new RuntimeException("Channel id should not be empty.");
        }

        if (fragmentWrapper != null) {
            performLoginWithFragment(channelId, fragmentWrapper);
        } else {
            performLoginWithActivity(channelId, getActivity());
        }
    };

    public LoginButton(Context context) {
        super(context);
        init();
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Registers a callback to be invoked when this button is tapped.
     * @param externalListener The callback to be invoked. This value may be null.
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener externalListener) {
        super.setOnClickListener(view -> {
            internalListener.onClick(view);
            if (externalListener != null) {
                externalListener.onClick(view);
            }
        });
    }

    /**
     * Specifies the <i>fragment</i> that contains this button so that its
     * <code>android.app.Fragment.onActivityResult(int, int, Intent)</code>
     * method is properly called.
     *
     * @param fragment The {@link Fragment} that contains this button.
     */
    public void setFragment(@NonNull Fragment fragment) {
        fragmentWrapper = new FragmentWrapper(fragment);
    }

    /**
     * Specifies the <i>fragment</i> that contains this button so that its
     * <code>androidx.fragment.app.Fragment#onActivityResult(int, int, Intent)</code>
     * method is properly called.
     *
     * @param fragment The <code>androidx.fragment.app.Fragment</code> that contains this button.
     */
    public void setFragment(@NonNull androidx.fragment.app.Fragment fragment) {
        fragmentWrapper = new FragmentWrapper(fragment);
    }

    /**
     * Sets the login delegate. This should be created using the {@link LoginDelegate.Factory#create()}
     * method. If the delegate is not set, a {@link RuntimeException} is thrown.
     * You also must call the {@link LoginDelegate#onActivityResult(int, int, Intent)} method of
     * the given <i>loginDelegate</i> in the <code>Activity</code> or <code>Fragment</code> instance
     * to handle the response intent.
     */
    public void setLoginDelegate(@NonNull LoginDelegate loginDelegate) {
        if (!(loginDelegate instanceof LoginDelegateImpl)) {
            throw new RuntimeException("Unexpected LoginDelegate," +
                    " please use the provided Factory to create the instance");
        }

        ((LoginDelegateImpl) loginDelegate).setLoginHandler(loginHandler);
        this.loginDelegate = loginDelegate;
    }

    /**
     * Sets the given <i>loginListener</i> to listen to the login result.
     *
     * @param loginListener The listener to set to listen to the login result.
     */
    public void addLoginListener(@NonNull LoginListener loginListener) {
        if (loginDelegate == null) {
            throw new RuntimeException("You must set LoginDelegate through setLoginDelegate() " +
                    " first");
        }

        loginHandler.addLoginListener(loginListener);
    }

    /**
     * Removes the given <i>loginListener</i> and stops it from listening to the login result.
     *
     * @param loginListener The listener to be removed.
     */
    public void removeLoginListener(@NonNull LoginListener loginListener) {
        loginHandler.removeLoginListener(loginListener);
    }

    /**
     * Sets whether the user logs in with LINE or with the browser according to the given
     * <i>isEnabled</i> parameter.
     *
     * @param isEnabled True if you want the user to log in with LINE instead of the browser; false
     *                  otherwise. The default value is true.
     */
    public void enableLineAppAuthentication(boolean isEnabled) {
        isLineAppAuthEnabled = isEnabled;
    }

    /**
     * Sets the channel ID of the channel that your application your application will use to log in.
     *
     * @param channelId The channel ID.
     */
    public void setChannelId(@NonNull String channelId) {
        this.channelId = channelId;
    }

    /**
     * Sets the authentication parameters that you want your application to use when it performs a login.
     *
     * @param params The authentication parameters.
     */
    public void setAuthenticationParams(@NonNull LineAuthenticationParams params) {
        authenticationParams = params;
    }

    private void init() {
        setAllCaps(false);
        setGravity(Gravity.CENTER);
        setText(R.string.btn_line_login);
        setTextColor(ContextCompat.getColor(getContext(), R.color.text_login_btn));
        setBackgroundResource(R.drawable.background_login_btn);
        super.setOnClickListener(internalListener);
    }

    @NonNull
    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper && !(context instanceof Activity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }

        throw new RuntimeException("Cannot find an Activity");
    }

    private void performLoginWithActivity(@NonNull String channelId, @NonNull Activity activity) {
        loginHandler.performLogin(
                activity,
                isLineAppAuthEnabled,
                channelId,
                authenticationParams
        );
    }

    private void performLoginWithFragment(
            @NonNull String channelId,
            @NonNull FragmentWrapper fragmentWrapper
    ) {
        loginHandler.performLogin(
                getActivity(),
                fragmentWrapper,
                isLineAppAuthEnabled,
                channelId,
                authenticationParams
        );
    }
}
