package com.linecorp.linesdktest;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

public class LineSDKUITestingActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LINE_SIGN_IN = 1234;

    private ActivityActionDelegate activityActionDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_sdkuitesting);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE_LINE_SIGN_IN) { return; }

        LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
        Log.i("LineSDKUITesting", result.toString());

        if (activityActionDelegate != null) {
            activityActionDelegate.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setActivityActionDelegate(ActivityActionDelegate activityActionDelegate) {
        this.activityActionDelegate = activityActionDelegate;
    }

    @FunctionalInterface
    public interface ActivityActionDelegate {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
