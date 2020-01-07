package com.linecorp.linesdk.api;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.nwclient.core.ChannelServiceHttpClient;

import java.util.Map;

import static com.linecorp.linesdk.utils.UriUtils.buildParams;

public abstract class BaseApiClient {
    private static final String REQUEST_HEADER_ACCESS_TOKEN = "Authorization";
    private static final String TOKEN_TYPE_BEARER = "Bearer";

    @NonNull
    protected final Uri apiBaseUrl;

    @NonNull
    protected final ChannelServiceHttpClient httpClient;

    @VisibleForTesting
    protected BaseApiClient(
            @NonNull Uri apiBaseUrl,
            @NonNull ChannelServiceHttpClient httpClient) {
        this.apiBaseUrl = apiBaseUrl;
        this.httpClient = httpClient;
    }

    @NonNull
    protected static Map<String, String> buildRequestHeaders(@NonNull InternalAccessToken accessToken) {
        return buildParams(
            REQUEST_HEADER_ACCESS_TOKEN,
            TOKEN_TYPE_BEARER + ' ' + accessToken.getAccessToken());
    }
}
