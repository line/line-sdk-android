package com.linecorp.linesdk.auth;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.linecorp.linesdktest.BuildConfig;

/**
 * Class to create {@link LineAuthenticationConfig} for test.
 */
public final class LineAuthenticationTestConfigFactory {
    private LineAuthenticationTestConfigFactory() {
        // To prevent instantiation
    }

    @NonNull
    public static LineAuthenticationConfig createTestConfig(@NonNull final String channelId,
                                                            final boolean isLineAppAuthDisabled) {
        final LineAuthenticationConfig.Builder configBuilder =
                new LineAuthenticationConfig.Builder(channelId)
                        .openidDiscoveryDocumentUrl(Uri.parse(BuildConfig.OPENID_DISCOVERY_DOCUMENT_URL))
                        .apiBaseUrl(Uri.parse(BuildConfig.API_SERVER_BASE_URI))
                        .webLoginPageUrl(Uri.parse(BuildConfig.WEB_LOGIN_PAGE_URL));

        if (isLineAppAuthDisabled) {
            configBuilder.disableLineAppAuthentication();
        }

        return configBuilder.build();
    }
}
