package com.linecorp.linesdktest.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Locale;

import androidx.annotation.NonNull;

public final class TestSettingManager {
    private static final String DEFAULT_CHANNEL_ID = "1620019587";
    private static final TestSetting DEFAULT_SETTING = new TestSetting(DEFAULT_CHANNEL_ID, null);

    private static final String SHARED_PREFERENCE_KEY = "test_settings";
    private static final String DATA_KEY_PREFIX_CHANNEL_ID = "channel_id_";
    private static final String DATA_KEY_PREFIX_UI_LOCALE = "ui_locale_";

    private TestSettingManager() {
        // To prevent instantiation
    }

    @NonNull
    public static TestSetting getSetting(@NonNull Context context, int id) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        String channelId = sharedPreferences.getString(DATA_KEY_PREFIX_CHANNEL_ID + id, "");
        if (TextUtils.isEmpty(channelId)) {
            return DEFAULT_SETTING;
        }

        Locale uiLocale;
        try {
            String localeStr = sharedPreferences.getString(DATA_KEY_PREFIX_UI_LOCALE + id, null);
            uiLocale = LocaleUtils.toLocale(localeStr);
        } catch (Exception e) {
            uiLocale = null;
        }

        return new TestSetting(channelId, uiLocale);
    }

    public static void save(@NonNull Context context, int id, @NonNull TestSetting setting) {
        context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE)
               .edit()
               .putString(DATA_KEY_PREFIX_CHANNEL_ID + id, setting.getChannelId())
               .putString(DATA_KEY_PREFIX_UI_LOCALE + id, ObjectUtils.toString(setting.getUILocale(), null))
               .apply();
    }
}
