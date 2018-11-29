package com.linecorp.linesdktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.linecorp.linesdk.LoginDelegate;
import com.linecorp.linesdk.LoginListener;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.linecorp.linesdk.widget.LoginButton;
import com.linecorp.linesdktest.settings.TestSetting;
import com.linecorp.linesdktest.settings.TestSettingManager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MenuFragment extends Fragment {
    @Nullable
    @BindView(R.id.setting_save_slot_radio)
    RadioGroup settingSaveSlotRadio;

    @Nullable
    @BindView(R.id.channel_id_edittext)
    EditText channelIdEditText;

    @Nullable
    @BindView(R.id.line_login_btn)
    LoginButton lineLoginButton;

    @Nullable
    @BindView(R.id.internal_apis_btn)
    Button internalApisButton;

    @NonNull
    private final LoginDelegate loginDelegate = LoginDelegate.Factory.create();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);

        if (BuildConfig.INCLUDE_INTERNAL_API_TEST) {
            internalApisButton.setVisibility(View.VISIBLE);
        }

        settingSaveSlotRadio.check(R.id.setting_save_slot1);
        setupLoginButton();

        return rootView;
    }

    private void setupLoginButton() {
        lineLoginButton.setFragment(this);
        lineLoginButton.setChannelId(channelIdEditText.getText().toString());
        lineLoginButton.enableLineAppAuthentication(true);
        lineLoginButton.setAuthenticationParams(new LineAuthenticationParams.Builder()
                .scopes(Arrays.asList(Scope.PROFILE,Scope.OPENID_CONNECT,Scope.OC_EMAIL))
                .build()
        );
        lineLoginButton.setLoginDelegate(loginDelegate);
        lineLoginButton.addLoginListener(new LoginListener() {
            @Override
            public void onLoginSuccess(@NonNull LineLoginResult result) {
                Toast.makeText(getContext(), result.getLineIdToken().getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginFailure(@Nullable LineLoginResult result) {
                Toast.makeText(getContext(), "Login failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int convertRadioButtonIdToSaveSlotId(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.setting_save_slot2:
                return 2;
            case R.id.setting_save_slot3:
                return 3;
            case R.id.setting_save_slot1:
            default:
                return 1;
        }
    }

    @NonNull
    private TestSetting createTestSettingByInputFields() {
        return new TestSetting(channelIdEditText.getText().toString());
    }

    @OnCheckedChanged({R.id.setting_save_slot1, R.id.setting_save_slot2, R.id.setting_save_slot3})
    @SuppressWarnings("unused")
    public void onSettingSaveSlotRadioCheckedChanged(final CompoundButton button, final boolean checked) {
        if (!checked) {
            return;
        }

        final TestSetting testSetting =
                TestSettingManager.getSetting(
                        getContext(),
                        convertRadioButtonIdToSaveSlotId(button.getId())
                );
        channelIdEditText.setText(testSetting.getChannelId());

        updateLineLoginButton();
    }

    @OnTextChanged(value = R.id.channel_id_edittext, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onChannelIdChanged(Editable editable) {
        updateLineLoginButton();
    }

    private void updateLineLoginButton() {
        String channelId = channelIdEditText.getText().toString();
        if (channelId.isEmpty()) {
            lineLoginButton.setEnabled(false);
        } else {
            lineLoginButton.setEnabled(true);
            lineLoginButton.setChannelId(channelId);
        }
    }

    @OnClick(R.id.save_settings_btn)
    @SuppressWarnings("unused")
    public void onSaveSettingBtnClick() {
        TestSettingManager.save(
                getContext(),
                convertRadioButtonIdToSaveSlotId(settingSaveSlotRadio.getCheckedRadioButtonId()),
                createTestSettingByInputFields()
        );
    }

    @OnClick(R.id.sign_in_btn)
    @SuppressWarnings("unused")
    public void onSignInBtnClick() {
        TestSetting testSetting = createTestSettingByInputFields();
        if (TextUtils.isEmpty(testSetting.getChannelId())) {
            Toast.makeText(getContext(), "Please input channel Id first.", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        startActivity(MainActivity.getSignInIntent(getContext(), testSetting));
    }

    @OnClick(R.id.apis_btn)
    @SuppressWarnings("unused")
    public void onApisBtnClick() {
        TestSetting testSetting = createTestSettingByInputFields();
        if (TextUtils.isEmpty(testSetting.getChannelId())) {
            Toast.makeText(getContext(), "Please input channel Id first.", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        startActivity(
                MainActivity.getApisIntent(getContext(), testSetting)
        );
    }

    @OnClick(R.id.internal_apis_btn)
    @SuppressWarnings("unused")
    public void onInternalApisBtnClick() {
        TestSetting testSetting = createTestSettingByInputFields();
        if (TextUtils.isEmpty(testSetting.getChannelId())) {
            Toast.makeText(getContext(), "Please input channel Id first.", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        startActivity(
                MainActivity.getInternalApisIntent(getContext(), testSetting)
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginDelegate.onActivityResult(requestCode, resultCode, data)) {
            // Login result is consumed.
            return;
        }
    }
}
