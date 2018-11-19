package com.linecorp.linesdk.api;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.linecorp.linesdktest.BuildConfig;

/**
 * Class to create {@link LineApiClient} for test.
 */
public final class LineApiTestClientFactory {
    private LineApiTestClientFactory() {
        // To prevent instantiation
    }

    @NonNull
    public static LineApiClient createLineApiClient(
            @NonNull final Context context,
            @NonNull final String channelId) {
        return new LineApiClientBuilder(context, channelId)
                .apiBaseUri(Uri.parse(BuildConfig.API_SERVER_BASE_URI))
                .build();
    }
}
