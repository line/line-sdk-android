package com.linecorp.linesdk.auth;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.utils.ObjectUtils;

/**
 * @hide
 * Class represents a configuration of LINE SDK.
 */
public class LineAuthenticationConfig implements Parcelable {
    public static final Parcelable.Creator<LineAuthenticationConfig> CREATOR = new Parcelable.Creator<LineAuthenticationConfig>() {
        @Override
        public LineAuthenticationConfig createFromParcel(Parcel in) {
            return new LineAuthenticationConfig(in);
        }

        @Override
        public LineAuthenticationConfig[] newArray(int size) {
            return new LineAuthenticationConfig[size];
        }
    };

    private static int FLAGS_LINE_APP_AUTHENTICATION_DISABLED = 0x1;
    private static int FLAGS_ENCRYPTOR_PREPARATION_DISABLED = 0x2;

    @NonNull
    private final String channelId;
    @NonNull
    private final Uri openidDiscoveryDocumentUrl;
    @NonNull
    private final Uri apiBaseUrl;

    @NonNull
    private final Uri webLoginPageUrl;
    private final boolean isLineAppAuthenticationDisabled;
    private final boolean isEncryptorPreparationDisabled;

    private LineAuthenticationConfig(@NonNull Builder builder) {
        channelId = builder.channelId;
        openidDiscoveryDocumentUrl = builder.openidDiscoveryDocumentUrl;
        apiBaseUrl = builder.apiBaseUrl;
        webLoginPageUrl = builder.webLoginPageUrl;
        isLineAppAuthenticationDisabled = builder.isLineAppAuthenticationDisabled;
        isEncryptorPreparationDisabled = builder.isEncryptorPreparationDisabled;
    }

    private LineAuthenticationConfig(@NonNull Parcel in) {
        channelId = in.readString();
        openidDiscoveryDocumentUrl = in.readParcelable(Uri.class.getClassLoader());
        apiBaseUrl = in.readParcelable(Uri.class.getClassLoader());
        webLoginPageUrl = in.readParcelable(Uri.class.getClassLoader());
        int settings = in.readInt();
        isLineAppAuthenticationDisabled = (settings & FLAGS_LINE_APP_AUTHENTICATION_DISABLED) > 0;
        isEncryptorPreparationDisabled = (settings & FLAGS_ENCRYPTOR_PREPARATION_DISABLED) > 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelId);
        dest.writeParcelable(openidDiscoveryDocumentUrl, flags);
        dest.writeParcelable(apiBaseUrl, flags);
        dest.writeParcelable(webLoginPageUrl, flags);
        int settings = 0;
        settings |= isLineAppAuthenticationDisabled ? FLAGS_LINE_APP_AUTHENTICATION_DISABLED : 0;
        settings |= isEncryptorPreparationDisabled ? FLAGS_ENCRYPTOR_PREPARATION_DISABLED : 0;
        dest.writeInt(settings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public String getChannelId() {
        return channelId;
    }

    @NonNull
    public Uri getOpenidDiscoveryDocumentUrl() {
        return openidDiscoveryDocumentUrl;
    }

    @NonNull
    public Uri getApiBaseUrl() {
        return apiBaseUrl;
    }

    @NonNull
    public Uri getWebLoginPageUrl() {
        return webLoginPageUrl;
    }

    public boolean isLineAppAuthenticationDisabled() {
        return isLineAppAuthenticationDisabled;
    }

    public boolean isEncryptorPreparationDisabled() {
        return isEncryptorPreparationDisabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        LineAuthenticationConfig that = (LineAuthenticationConfig) o;

        if (isLineAppAuthenticationDisabled != that.isLineAppAuthenticationDisabled) { return false; }
        if (isEncryptorPreparationDisabled != that.isEncryptorPreparationDisabled) { return false; }
        if (!channelId.equals(that.channelId)) { return false; }
        if (!openidDiscoveryDocumentUrl.equals(that.openidDiscoveryDocumentUrl)) { return false; }
        if (!apiBaseUrl.equals(that.apiBaseUrl)) { return false; }
        return webLoginPageUrl.equals(that.webLoginPageUrl);
    }

    @Override
    public int hashCode() {
        int result = channelId.hashCode();
        result = 31 * result + openidDiscoveryDocumentUrl.hashCode();
        result = 31 * result + apiBaseUrl.hashCode();
        result = 31 * result + webLoginPageUrl.hashCode();
        result = 31 * result + (isLineAppAuthenticationDisabled ? 1 : 0);
        result = 31 * result + (isEncryptorPreparationDisabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LineAuthenticationConfig{" +
               "channelId='" + channelId + '\'' +
               ", openidDiscoveryDocumentUrl=" + openidDiscoveryDocumentUrl +
               ", apiBaseUrl=" + apiBaseUrl +
               ", webLoginPageUrl=" + webLoginPageUrl +
               ", isLineAppAuthenticationDisabled=" + isLineAppAuthenticationDisabled +
               ", isEncryptorPreparationDisabled=" + isEncryptorPreparationDisabled +
               '}';
    }

    public static class Builder {
        @NonNull
        private final String channelId;
        @NonNull
        private Uri openidDiscoveryDocumentUrl;
        @NonNull
        private Uri apiBaseUrl;
        @NonNull
        private Uri webLoginPageUrl;
        private boolean isLineAppAuthenticationDisabled;
        private boolean isEncryptorPreparationDisabled;

        public Builder(@NonNull String channelId) {
            if (TextUtils.isEmpty(channelId)) {
                throw new IllegalArgumentException("channelId is empty.");
            }
            this.channelId = channelId;
            openidDiscoveryDocumentUrl = Uri.parse(BuildConfig.OPENID_DISCOVERY_DOCUMENT_URL);
            apiBaseUrl = Uri.parse(BuildConfig.API_SERVER_BASE_URI);
            webLoginPageUrl = Uri.parse(BuildConfig.WEB_LOGIN_PAGE_URL);
        }

        @NonNull
        Builder openidDiscoveryDocumentUrl(@Nullable final Uri openidDiscoveryDocumentUrl) {
            this.openidDiscoveryDocumentUrl =
                    ObjectUtils.defaultIfNull(openidDiscoveryDocumentUrl,
                                              Uri.parse(BuildConfig.OPENID_DISCOVERY_DOCUMENT_URL));
            return this;
        }

        @NonNull
        Builder apiBaseUrl(@Nullable final Uri apiBaseUrl) {
            this.apiBaseUrl = ObjectUtils.defaultIfNull(apiBaseUrl,
                                                        Uri.parse(BuildConfig.API_SERVER_BASE_URI));
            return this;
        }

        @NonNull
        Builder webLoginPageUrl(@Nullable final Uri webLoginPageUrl) {
            this.webLoginPageUrl = ObjectUtils.defaultIfNull(webLoginPageUrl,
                                                             Uri.parse(BuildConfig.WEB_LOGIN_PAGE_URL));
            return this;
        }

        @NonNull
        public Builder disableLineAppAuthentication() {
            isLineAppAuthenticationDisabled = true;
            return this;
        }

        @NonNull
        public Builder disableEncryptorPreparation() {
            isEncryptorPreparationDisabled = true;
            return this;
        }

        @NonNull
        public LineAuthenticationConfig build() {
            return new LineAuthenticationConfig(this);
        }
    }
}
