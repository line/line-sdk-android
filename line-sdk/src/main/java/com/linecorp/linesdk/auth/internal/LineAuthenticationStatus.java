package com.linecorp.linesdk.auth.internal;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.internal.pkce.PKCECode;

/**
 * Data class to hold mutable values which represents authentication status, during a LINE
 * Authentication.
 */
/* package */ class LineAuthenticationStatus implements Parcelable {
    /* package */ enum Status {
        INIT,
        STARTED,
        INTENT_RECEIVED,
        INTENT_HANDLED
    }

    @Nullable
    private PKCECode pkceCode;
    @Nullable
    private String sentRedirectUri;
    /**
     * An opaque value used by the client to maintain state between the request and callback. <br></br>
     * Please refer to specification: <a href="https://tools.ietf.org/html/rfc6749#section-4.1.1">The OAuth 2.0 Authorization Framework</a>
     */
    @Nullable
    private String oAuthState;
    /**
     * String value used to associate a Client session with an ID Token, and to mitigate replay attacks. <br></br>
     * Please refer to specification: <a href="https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest">OpenID Connect Core 1.0</a>
     */
    @Nullable
    private String openIdNonce;
    private Status status = Status.INIT;

    LineAuthenticationStatus() {
    }

    @Nullable
    PKCECode getPKCECode() {
        return pkceCode;
    }

    void setPKCECode(@Nullable final PKCECode pkceCode) {
        this.pkceCode = pkceCode;
    }

    @Nullable
    String getSentRedirectUri() {
        return sentRedirectUri;
    }

    void setSentRedirectUri(@Nullable final String sentRedirectUri) {
        this.sentRedirectUri = sentRedirectUri;
    }

    void authenticationIntentReceived() {
        status = Status.INTENT_RECEIVED;
    }

    void authenticationIntentHandled() {
        status = Status.INTENT_HANDLED;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public String getOAuthState() {
        return oAuthState;
    }

    public void setOAuthState(@Nullable final String oAuthState) {
        this.oAuthState = oAuthState;
    }

    @Nullable
    public String getOpenIdNonce() {
        return openIdNonce;
    }

    public void setOpenIdNonce(@Nullable final String openIdNonce) {
        this.openIdNonce = openIdNonce;
    }

    public void authenticationStarted() {
        status = Status.STARTED;
    }

    // Parcelable implementation
    private LineAuthenticationStatus(@NonNull final Parcel in) {
        pkceCode = in.readParcelable(PKCECode.class.getClassLoader());
        sentRedirectUri = in.readString();
        status = Status.values()[in.readByte()];
        oAuthState = in.readString();
        openIdNonce = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(pkceCode, flags);
        dest.writeString(sentRedirectUri);
        dest.writeByte((byte) status.ordinal());
        dest.writeString(oAuthState);
        dest.writeString(openIdNonce);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LineAuthenticationStatus> CREATOR = new Creator<LineAuthenticationStatus>() {
        @Override
        public LineAuthenticationStatus createFromParcel(final Parcel in) {
            return new LineAuthenticationStatus(in);
        }

        @Override
        public LineAuthenticationStatus[] newArray(final int size) {
            return new LineAuthenticationStatus[size];
        }
    };
}
