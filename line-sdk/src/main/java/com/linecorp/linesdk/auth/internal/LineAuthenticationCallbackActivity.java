package com.linecorp.linesdk.auth.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity to notify an Intent of authentication result to {@link LineAuthenticationActivity}.
 * {@code LineAuthenticationActivity} can not receive the intent directly because it must not be
 * singleInstance or singleTask.
 */
public class LineAuthenticationCallbackActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent callbackIntent = new Intent(this, LineAuthenticationActivity.class);
        callbackIntent.setData(getIntent().getData());
        callbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(callbackIntent);
        finish();
    }
}
