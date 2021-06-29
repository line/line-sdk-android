package com.linecorp.linesdktest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationConfig;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineAuthenticationParams.BotPrompt;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.linecorp.linesdktest.settings.TestSetting;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInFragment extends Fragment {
    private static final int REQUEST_CODE = 1;

    private static final String ARG_KEY_CHANNEL_ID = "channelId";
    private static final String ARG_KEY_UI_LOCALE = "uiLocale";

    private static final String LOG_SEPARATOR = System.getProperty("line.separator");

    private final List<CheckBox> scopeCheckBoxes = new ArrayList<>();

    @Nullable
    private String channelId;

    @Nullable
    private Locale uiLocale;

    @Nullable
    @BindView(R.id.signin_nonce)
    EditText nonceEditText;

    @Nullable
    @BindView(R.id.signin_bot_prompt_normal_radio)
    RadioButton botPromptNormalRadioButton;

    @Nullable
    @BindView(R.id.signin_bot_prompt_aggressive_radio)
    RadioButton botPromptAggressiveRadioButton;

    @Nullable
    @BindView(R.id.scope_all_checkbox)
    CheckBox scopeAllCheckbox;

    @Nullable
    @BindView(R.id.scope_checkbox_layout)
    LinearLayout scopeCheckboxLayout;

    @Nullable
    @BindView(R.id.sign_in_line_app_auth_check)
    CheckBox useLineAppAuthCheckbox;

    @Nullable
    @BindView(R.id.log)
    TextView logView;

    @NonNull
    static SignInFragment newFragment(@NonNull TestSetting setting) {
        SignInFragment fragment = new SignInFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_KEY_CHANNEL_ID, setting.getChannelId());
        arguments.putSerializable(ARG_KEY_UI_LOCALE, setting.getUILocale());
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        channelId = arguments.getString(ARG_KEY_CHANNEL_ID);
        uiLocale = (Locale) arguments.getSerializable(ARG_KEY_UI_LOCALE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        ButterKnife.bind(this, rootView);

        buildScopeCheckBoxes();

        return rootView;
    }

    private void buildScopeCheckBoxes() {
        final List<Scope> scopes = (BuildConfig.INCLUDE_INTERNAL_API_TEST) ?
                Arrays.asList(
                        Scope.PROFILE,
                        Scope.OPENID_CONNECT,
                        Scope.OC_EMAIL,
                        Scope.OC_PHONE_NUMBER,
                        Scope.OC_GENDER,
                        Scope.OC_BIRTHDATE,
                        Scope.OC_ADDRESS,
                        Scope.OC_REAL_NAME,
                        Scope.FRIEND,
                        Scope.GROUP,
                        Scope.MESSAGE,
                        Scope.ONE_TIME_SHARE,
                        Scope.OPEN_CHAT_TERM_STATUS,
                        Scope.OPEN_CHAT_ROOM_CREATE_JOIN,
                        Scope.OPEN_CHAT_SUBSCRIPTION_INFO
                ) :
                Arrays.asList(
                        Scope.PROFILE,
                        Scope.OPENID_CONNECT,
                        new Scope("self_defined_scope")
                );


        final FragmentActivity activity = getActivity();
        for (final Scope scope : scopes) {
            final CheckBox checkBox = new CheckBox(activity);
            checkBox.setText(scope.getCode());
            checkBox.setTag(scope);

            scopeCheckboxLayout.addView(checkBox);

            scopeCheckBoxes.add(checkBox);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
            addLog("===== Login result =====");
            addLog(result.toString());
            addLog("==========================");
        } else {
            addLog("Illegal response : onActivityResult("
                    + requestCode + ", " + resultCode + ", " + data + ")");
        }
    }

    @NonNull
    private LineAuthenticationConfig createLineAuthenticationConfigForTest(boolean useLineAppAuth) {
        LineAuthenticationConfig.Builder builder = new LineAuthenticationConfig.Builder(channelId);

        if (!useLineAppAuth) {
            builder.disableLineAppAuthentication();
        }
        return builder.build();
    }

    @NonNull
    private LineAuthenticationParams createAuthenticationParamsForTest() {
        return new LineAuthenticationParams.Builder()
                .scopes(getCheckedScopes())
                .nonce(StringUtils.trimToNull(nonceEditText.getText().toString()))
                .botPrompt(getBotPrompt())
                .uiLocale(uiLocale)
                .build();
    }

    private BotPrompt getBotPrompt() {
        if (botPromptNormalRadioButton.isChecked()) {
            return BotPrompt.normal;
        }

        if (botPromptAggressiveRadioButton.isChecked()) {
            return BotPrompt.aggressive;
        }

        return null;
    }

    @NonNull
    private List<Scope> getCheckedScopes() {
        final boolean useAllScopes = scopeAllCheckbox.isChecked();

        final List<Scope> scopes = new ArrayList<>();
        for (final CheckBox checkBox : scopeCheckBoxes) {
            if (useAllScopes || checkBox.isChecked()) {
                scopes.add((Scope) checkBox.getTag());
            }
        }

        return scopes;
    }

    private void addLog(@NonNull String logText) {
        if (logView != null) {
            logView.setText(logView.getText() + LOG_SEPARATOR + logText);
        }
    }

    @OnClick(R.id.generate_nonce_btn)
    @SuppressWarnings("unused")
    public void onGenerateNonceBtnClick() {
        nonceEditText.setText(RandomStringUtils.randomAlphanumeric(16));
    }

    @OnClick(R.id.scope_all_checkbox)
    @SuppressWarnings("unused")
    public void onScopeAllCheckboxClick() {
        if (scopeAllCheckbox.isChecked()) {
            scopeCheckboxLayout.setVisibility(View.INVISIBLE);
        } else {
            scopeCheckboxLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.sign_in_btn)
    @SuppressWarnings("unused")
    public void onSignInBtnClick() {
        try {
            Intent intent = LineLoginApi.getLoginIntent(
                    getContext(),
                    createLineAuthenticationConfigForTest(useLineAppAuthCheckbox.isChecked()),
                    createAuthenticationParamsForTest());
            startActivityForResult(intent, REQUEST_CODE);
            addLog("Sign-in is started. [" + LOG_SEPARATOR
                    + "    channelId : " + channelId + LOG_SEPARATOR
                    + "]");
        } catch (Exception e) {
            addLog(e.toString());
        }
    }

    @OnClick(R.id.clear_btn)
    @SuppressWarnings("unused")
    public void onClearLogBtnClick() {
        if (logView != null) {
            logView.setText("");
        }
    }
}
