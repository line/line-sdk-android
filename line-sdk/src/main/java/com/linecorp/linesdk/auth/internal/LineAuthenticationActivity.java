package com.linecorp.linesdk.auth.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.R;
import com.linecorp.linesdk.auth.LineAuthenticationConfig;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;

import static com.linecorp.linesdk.auth.internal.LineAuthenticationStatus.Status.INIT;
import static com.linecorp.linesdk.auth.internal.LineAuthenticationStatus.Status.INTENT_HANDLED;
import static com.linecorp.linesdk.auth.internal.LineAuthenticationStatus.Status.INTENT_RECEIVED;
import static com.linecorp.linesdk.auth.internal.LineAuthenticationStatus.Status.STARTED;

/**
 * Activity to control the LINE authentication flow.
 */
public class LineAuthenticationActivity extends Activity {
    private static final String PARAM_KEY_AUTHENTICATION_CONFIG = "authentication_config";
    private static final String PARAM_KEY_AUTHENTICATION_PARAMS = "authentication_params";
    private static final String RESPONSE_DATA_KEY_AUTHENTICATION_RESULT = "authentication_result";
    private static final String INSTANCE_STATE_KEY_AUTHENTICATION_STATUS = "authentication_status";
    private static final String SUPPORTED_SCHEME = "lineauth";

    // for checking whether authentication finish action should be continued
    private boolean isActivityStopped = false;

    @Nullable
    private LineAuthenticationStatus authenticationStatus;
    @NonNull
    private LineAuthenticationController authenticationController;

    @NonNull
    public static Intent getLoginIntent(
            @NonNull Context context,
            @NonNull LineAuthenticationConfig config,
            @NonNull LineAuthenticationParams params) {
        Intent intent = new Intent(context, LineAuthenticationActivity.class);
        intent.putExtra(PARAM_KEY_AUTHENTICATION_CONFIG, config);
        intent.putExtra(PARAM_KEY_AUTHENTICATION_PARAMS, params);
        return intent;
    }

    @NonNull
    public static LineLoginResult getResultFromIntent(@NonNull Intent intent) {
        LineLoginResult lineLoginResult =
                intent.getParcelableExtra(RESPONSE_DATA_KEY_AUTHENTICATION_RESULT);
        return lineLoginResult == null
               ? LineLoginResult.internalError("Authentication result is not found.")
               : lineLoginResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linesdk_activity_lineauthentication);

        Intent intent = getIntent();

        // When launched from LINE app, LineAuthenticationActivity will be created twice.
        // The result should be kept so that it can be processed in correct Controller
        Uri uri = intent.getData();
        if(uri != null && uri.getScheme().equals(SUPPORTED_SCHEME)) {
            LineAuthenticationController.setIntent(intent);
            finish();
            return;
        }

        LineAuthenticationConfig config =
                intent.getParcelableExtra(PARAM_KEY_AUTHENTICATION_CONFIG);
        LineAuthenticationParams params =
                intent.getParcelableExtra(PARAM_KEY_AUTHENTICATION_PARAMS);
        if (config == null || params == null) {
            onAuthenticationFinished(
                    LineLoginResult.internalError("The requested parameter is illegal.")
            );
            return;
        }
        authenticationStatus = getAuthenticationStatus(savedInstanceState);
        authenticationController = new LineAuthenticationController(
                this,
                config,
                authenticationStatus,
                params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (authenticationStatus.getStatus() == INIT) {
            authenticationController.startLineAuthentication();
        } else if (authenticationStatus.getStatus() != INTENT_RECEIVED) {
            // 1. with passcode in LINE app, when user presses back on passcode screen, onStart will be called, without onActivityResult nor onNewIntent
            // 2. if passcode is correct, it takes more time to get onNewIntent
            // onStart is called before calling onNewIntent, we need to wait a delay to make sure onStart is really for cancel.
            authenticationController.handleCancel();
        }

        isActivityStopped = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityStopped = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (authenticationStatus.getStatus() == STARTED) {
            authenticationController.handleIntentFromLineApp(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (authenticationStatus.getStatus() == STARTED) {
            authenticationController.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(INSTANCE_STATE_KEY_AUTHENTICATION_STATUS, authenticationStatus);
    }

    @NonNull
    private LineAuthenticationStatus getAuthenticationStatus(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return new LineAuthenticationStatus();
        }
        LineAuthenticationStatus savedState =
                savedInstanceState.getParcelable(INSTANCE_STATE_KEY_AUTHENTICATION_STATUS);
        return savedState == null ? new LineAuthenticationStatus() : savedState;
    }

    @MainThread
    void onAuthenticationFinished(@NonNull LineLoginResult lineLoginResult) {
        // During LINE app agreement screen pops up, if user presses home and goes back to app (integrating loginsdk),
        // onAuthenticationFinished will be called with CANCEL status.
        // If user goes to LINE app again, and interact with agreement, it will bring user back to here again,
        // but authenticationStatus is already gone. In this case, we should just ignore the result,
        // and finish the Activity
        if(authenticationStatus == null) {
            finish();
            return;
        }

        // STARTED: no intent comes back. if the activity is not stopped, need to finish the authentication activity
        // INTENT_HANDLED: the intent is back and handled, need to finish the activity with result
        if((authenticationStatus.getStatus() == STARTED && !isActivityStopped) ||
                   authenticationStatus.getStatus() == INTENT_HANDLED) {
            Intent resultData = new Intent();
            resultData.putExtra(RESPONSE_DATA_KEY_AUTHENTICATION_RESULT, lineLoginResult);
            setResult(Activity.RESULT_OK, resultData);
            finish();
        }
    }
}
