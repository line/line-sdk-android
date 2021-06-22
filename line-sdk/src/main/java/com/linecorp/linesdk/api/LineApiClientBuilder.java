package com.linecorp.linesdk.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.ManifestParser;
import com.linecorp.linesdk.api.internal.AutoRefreshLineApiClientProxy;
import com.linecorp.linesdk.api.internal.LineApiClientImpl;
import com.linecorp.linesdk.internal.AccessTokenCache;
import com.linecorp.linesdk.internal.EncryptorHolder;
import com.linecorp.linesdk.internal.nwclient.LineAuthenticationApiClient;
import com.linecorp.linesdk.internal.nwclient.TalkApiClient;

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
     *
     * @param context   The Android context.
     * @param channelId The channel ID.
     * @throws IllegalArgumentException If <i>channelId</i> is {@code null}.
     */
    public LineApiClientBuilder(@NonNull Context context, @NonNull String channelId) {
        if (TextUtils.isEmpty(channelId)) {
            throw new IllegalArgumentException("channel id is empty");
        }
        this.context = context.getApplicationContext();
        this.channelId = channelId;

        ManifestParser parser = new ManifestParser();
        LineEnvConfig config = parser.parse(context);
        if (config == null) {
            config = new LineDefaultEnvConfig();
        }

        openidDiscoveryDocumentUrl = Uri.parse(config.getOpenIdDiscoveryDocumentUrl());
        apiBaseUri = Uri.parse(config.getApiServerBaseUri());
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
     * @hide
     * Sets the OpenID Discovery Document URL.
     *
     * @param openidDiscoveryDocumentUrl The URI to set.
     * @return The current {@link LineApiClientBuilder} instance.
     */
    @Deprecated
    @NonNull
    public LineApiClientBuilder openidDiscoveryDocumentUrl(@Nullable final Uri openidDiscoveryDocumentUrl) {
        if (openidDiscoveryDocumentUrl != null) {
            this.openidDiscoveryDocumentUrl = openidDiscoveryDocumentUrl;
        }
        return this;
    }
    /**
     * @hide
     * Sets the API base URI.
     */
    @Deprecated
    @NonNull
    public LineApiClientBuilder apiBaseUri(@Nullable final Uri apiBaseUri) {
        if (apiBaseUri != null) {
            this.apiBaseUri = apiBaseUri;
        }
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
