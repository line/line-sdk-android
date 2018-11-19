package com.linecorp.linesdk.api;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.api.internal.AutoRefreshLineApiClientProxy;
import com.linecorp.linesdk.api.internal.LineApiClientImpl;
import com.linecorp.linesdk.internal.AccessTokenCache;
import com.linecorp.linesdk.internal.EncryptorHolder;
import com.linecorp.linesdk.internal.nwclient.LineAuthenticationApiClient;
import com.linecorp.linesdk.internal.nwclient.TalkApiClient;
import com.linecorp.linesdk.utils.ObjectUtils;

/**
 * Represents a builder for creating {@link LineApiClient} objects with the desired settings.
 */
public class LineApiClientBuilder {
    @NonNull
    private final Context context;
    @NonNull
    private final String channelId;
    @NonNull
    private Uri openidDiscoveryDocumentUrl;
    @NonNull
    private Uri apiBaseUri;
    private boolean isTokenAutoRefreshDisabled;
    private boolean isEncryptorPreparationDisabled;

    /**
     * Constructs a builder for a {@link LineApiClient} object with the given <i>channelId</i>.
     * @param context The Android context.
     * @param channelId The channel ID.
     * @throws IllegalArgumentException If <i>channelId</i> is {@code null}.
     */
    public LineApiClientBuilder(@NonNull Context context, @NonNull String channelId) {
        if (TextUtils.isEmpty(channelId)) {
            throw new IllegalArgumentException("channel id is empty");
        }
        this.context = context.getApplicationContext();
        this.channelId = channelId;
        openidDiscoveryDocumentUrl = Uri.parse(BuildConfig.OPENID_DISCOVERY_DOCUMENT_URL);
        apiBaseUri = Uri.parse(BuildConfig.API_SERVER_BASE_URI);
    }

    /**
     * Disables the SDK's feature that automatically refreshes the access token.
     *
     * @return The current {@link LineApiClientBuilder} instance.
     */
    @NonNull
    public LineApiClientBuilder disableTokenAutoRefresh() {
        isTokenAutoRefreshDisabled = true;
        return this;
    }

    /**
     * Disables the SDK's feature that prepares an encryptor.
     *
     * @return The current {@link LineApiClientBuilder} instance.
     */
    @NonNull
    public LineApiClientBuilder disableEncryptorPreparation() {
        isEncryptorPreparationDisabled = true;
        return this;
    }

    /**
     * Sets the API base URI.
     *
     * @param openidDiscoveryDocumentUrl The URI to set.
     * @return The current {@link LineApiClientBuilder} instance.
     */
    @NonNull
    LineApiClientBuilder openidDiscoveryDocumentUrl(@Nullable final Uri openidDiscoveryDocumentUrl) {
        this.openidDiscoveryDocumentUrl =
                ObjectUtils.defaultIfNull(openidDiscoveryDocumentUrl,
                                          Uri.parse(BuildConfig.OPENID_DISCOVERY_DOCUMENT_URL));
        return this;
    }

    @NonNull
    LineApiClientBuilder apiBaseUri(@Nullable final Uri apiBaseUri) {
        this.apiBaseUri = ObjectUtils.defaultIfNull(apiBaseUri,
                                                    Uri.parse(BuildConfig.API_SERVER_BASE_URI));
        return this;
    }

    /**
     * Creates a {@link LineApiClient} instance.
     *
     * @return The {@link LineApiClient} instance.
     */
    @NonNull
    public LineApiClient build() {
        // To minimize thread blocking time by the secret key generation.
        if (!isEncryptorPreparationDisabled) {
            EncryptorHolder.initializeOnWorkerThread(context);
        }
        LineApiClient lineApiClient = new LineApiClientImpl(
                channelId,
                new LineAuthenticationApiClient(context, openidDiscoveryDocumentUrl, apiBaseUri),
                new TalkApiClient(context, apiBaseUri),
                new AccessTokenCache(context, channelId));
        return isTokenAutoRefreshDisabled
               ? lineApiClient
               : AutoRefreshLineApiClientProxy.newProxy(lineApiClient);
    }
}
