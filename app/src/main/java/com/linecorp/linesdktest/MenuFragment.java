package com.linecorp.linesdktest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.linecorp.linesdk.LoginDelegate;
import com.linecorp.linesdk.LoginListener;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.linecorp.linesdk.widget.LoginButton;
import com.linecorp.linesdktest.settings.TestSetting;
import com.linecorp.linesdktest.settings.TestSettingManager;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class MenuFragment extends Fragment {
    private static final String LOCALE_USE_DEFAULT = "[Use Default]";

    @Nullable
    @BindView(R.id.setting_save_slot_radio)
    RadioGroup settingSaveSlotRadio;

    @Nullable
    @BindView(R.id.channel_id_edittext)
    EditText channelIdEditText;

    @Nullable
    @BindView(R.id.uiLocale_spinner)
    Spinner uiLocaleSpinner;

    @Nullable
    @BindView(R.id.line_login_btn)
    LoginButton lineLoginButton;

    @Nullable
    @BindView(R.id.internal_apis_btn)
    Button internalApisButton;

    private final List<String> locales = new ArrayList<>();

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

        setupUILocaleSpinner(container.getContext());
        setupLoginButton();

        settingSaveSlotRadio.check(R.id.setting_save_slot1);

        return rootView;
    }

    private void setupUILocaleSpinner(Context context) {
        locales.clear();
        locales.add(LOCALE_USE_DEFAULT);
        locales.addAll(Arrays.asList(getResources().getStringArray(R.array.supportedLocales)));

        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, locales);
        uiLocaleSpinner.setAdapter(adapter);
    }

    private void setupLoginButton() {
        lineLoginButton.setFragment(this);
        lineLoginButton.enableLineAppAuthentication(true);
        lineLoginButton.setLoginDelegate(loginDelegate);
        lineLoginButton.addLoginListener(new LoginListener() {
            @Override
            public void onLoginSuccess(@NonNull LineLoginResult result) {
                Toast.makeText(getContext(), "Login success", Toast.LENGTH_SHORT).show();
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

    @Nullable
    private Locale getSelectedUILocale() {
        final String uiLocale = (String) uiLocaleSpinner.getSelectedItem();
        if (StringUtils.equals(uiLocale, LOCALE_USE_DEFAULT)) {
            // use default locale
            return null;
        } else {
            return LocaleUtils.toLocale(uiLocale);
        }
    }

    private void setSelectedUILocale(@Nullable Locale locale) {
        String localeStr = ObjectUtils.toString(locale, null);
        int index = locales.indexOf(localeStr);
        if (index < 0) {
            index = 0;
        }
        uiLocaleSpinner.setSelection(index);
    }

    @NonNull
    private TestSetting createTestSettingByInputFields() {
        return new TestSetting(channelIdEditText.getText().toString(), getSelectedUILocale());
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
        setSelectedUILocale(testSetting.getUILocale());

        updateLineLoginButton();
    }

    @OnTextChanged(value = R.id.channel_id_edittext, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onChannelIdChanged(Editable editable) {
        updateLineLoginButton();
    }

    @OnItemSelected(R.id.uiLocale_spinner)
    public void onUILocaleSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateLineLoginButton();
    }

    private void updateLineLoginButton() {
        // set channel id
        String channelId = channelIdEditText.getText().toString();
        if (channelId.isEmpty()) {
            lineLoginButton.setEnabled(false);
        } else {
            lineLoginButton.setEnabled(true);
            lineLoginButton.setChannelId(channelId);
        }

        lineLoginButton.setAuthenticationParams(
                new LineAuthenticationParams.Builder()
                        // set scopes
                        .scopes(Arrays.asList(Scope.PROFILE, Scope.OPENID_CONNECT))
                        // set nonce
                        .nonce(RandomStringUtils.randomAlphanumeric(16))
                        // set locale
                        .uiLocale(getSelectedUILocale())
                        .build()
        );
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
