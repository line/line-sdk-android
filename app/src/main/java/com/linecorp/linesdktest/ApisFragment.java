package com.linecorp.linesdktest;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linecorp.linesdktest.settings.TestSetting;
import com.linecorp.linesdktest.util.FlexMessageGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApisFragment extends BaseApisFragment {
    private final FlexMessageGenerator flexMessageGenerator = new FlexMessageGenerator();

    @Nullable
    @BindView(R.id.log)
    TextView logView;

    @NonNull
    static ApisFragment newFragment(@NonNull TestSetting setting) {
        ApisFragment fragment = new ApisFragment();
        fragment.setArguments(buildArguments(setting));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apis, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }


    @OnClick(R.id.clear_log_btn)
    void clear() {
        logView.setText("");
    }

    @OnClick(R.id.get_profile_btn)
    void getProfile() {
        startApiAsyncTask("getProfile", () -> lineApiClient.getProfile());
    }

    @OnClick(R.id.get_friendship_status_btn)
    void getFriendshipStatus() {
        startApiAsyncTask("getFriendshipStatus", () -> lineApiClient.getFriendshipStatus());
    }

    @OnClick(R.id.logout_btn)
    void logout() {
        startApiAsyncTask("logout", () -> lineApiClient.logout());
    }

    @OnClick(R.id.refresh_token_btn)
    void refreshToken() {
        startApiAsyncTask("refreshToken", () -> lineApiClient.refreshAccessToken());
    }

    @OnClick(R.id.verify_token_btn)
    void verifyToken() {
        startApiAsyncTask("verifyToken", () -> lineApiClient.verifyToken());
    }

    @OnClick(R.id.get_current_token_btn)
    void getCurrentToken() {
        startApiAsyncTask("getCurrentToken", () -> lineApiClient.getCurrentAccessToken());
    }

    @Override
    protected void addLog(@NonNull String logText) {
        Log.d("LineSdkTest", logText);
        if (logView != null) {
            logView.setText(logView.getText() + LOG_SEPARATOR + logText);
        }
    }
}
