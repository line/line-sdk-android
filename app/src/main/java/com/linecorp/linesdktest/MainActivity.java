package com.linecorp.linesdktest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import com.linecorp.linesdktest.settings.TestSetting;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRAS_KEY_SCREEN = "screen";
    private static final String EXTRAS_KEY_SETTING = "setting";

    enum TestAppScreen {
        SignIn() {
            @NonNull
            @Override
            Fragment getFragment(@NonNull TestSetting setting) {
                return SignInFragment.newFragment(setting);
            }
        },
        Apis() {
            @NonNull
            @Override
            Fragment getFragment(@NonNull TestSetting setting) {
                return ApisFragment.newFragment(setting);
            }
        },
        InternalApis() {
            @NonNull
            @Override
            Fragment getFragment(@NonNull TestSetting setting) {
                return InternalApisFragment.newFragment(setting);
            }
        };

        @NonNull
        abstract Fragment getFragment(@NonNull TestSetting setting);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getSupportFragmentManager().getFragments().isEmpty()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new MenuFragment())
                    .commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        TestAppScreen screen = (TestAppScreen) intent.getSerializableExtra(EXTRAS_KEY_SCREEN);
        TestSetting setting = intent.getParcelableExtra(EXTRAS_KEY_SETTING);
        if (screen == null || setting == null) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, screen.getFragment(setting))
                .addToBackStack(null)
                .commit();
    }

    @NonNull
    public static Intent getSignInIntent(
            @NonNull Context context, @NonNull TestSetting setting) {
        return getIntent(context, TestAppScreen.SignIn, setting);
    }

    @NonNull
    public static Intent getApisIntent(
            @NonNull Context context, @NonNull TestSetting setting) {
        return getIntent(context, TestAppScreen.Apis, setting);
    }

    @NonNull
    public static Intent getInternalApisIntent(
            @NonNull Context context, @NonNull TestSetting setting) {
        return getIntent(context, TestAppScreen.InternalApis, setting);
    }

    // To ensure the type of the intent extra value testAppScreen.
    @SuppressWarnings("TypeMayBeWeakened")
    @NonNull
    private static Intent getIntent(
            @NonNull Context context,
            @NonNull TestAppScreen testAppScreen,
            @NonNull TestSetting setting) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRAS_KEY_SCREEN, testAppScreen);
        intent.putExtra(EXTRAS_KEY_SETTING, setting);
        return intent;
    }
}
