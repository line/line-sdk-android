package com.linecorp.linesdk.internal.nwclient.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * Generates a user agent of a http request header.
 */
/* package */ class UserAgentGenerator {
    private static final String DEFAULT_PACKAGE_NAME = "UNK";
    private static final String DEFAULT_VERSION_NAME = "UNK";

    @Nullable
    private final PackageInfo packageInfo;
    @NonNull
    private final String sdkVersion;

    @Nullable
    private String cachedUserAgent;

    UserAgentGenerator(@NonNull Context context, @NonNull String sdkVersion) {
        packageInfo = getPackageInfo(context);
        this.sdkVersion = sdkVersion;
    }

    @NonNull
    public String getUserAgent() {
        if (cachedUserAgent != null) {
            return cachedUserAgent;
        }

        String packageName = packageInfo == null ? DEFAULT_PACKAGE_NAME : packageInfo.packageName;
        String versionName = packageInfo == null ? DEFAULT_VERSION_NAME : packageInfo.versionName;
        Locale locale = Locale.getDefault();

        cachedUserAgent = packageName + "/" + versionName
                + " ChannelSDK/" + sdkVersion
                + " (Linux; U; Android " + Build.VERSION.RELEASE + "; "
                + locale.getLanguage() + "-" + locale.getCountry() + "; "
                + Build.MODEL
                + " Build/" + Build.ID + ")";
        return cachedUserAgent;
    }

    @Nullable
    private static PackageInfo getPackageInfo(@NonNull Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw null;
        }
    }
}
