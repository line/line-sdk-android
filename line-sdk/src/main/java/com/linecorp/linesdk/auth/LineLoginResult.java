package com.linecorp.linesdk.auth;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.LineProfile;

import static com.linecorp.linesdk.utils.ParcelUtils.readEnum;
import static com.linecorp.linesdk.utils.ParcelUtils.writeEnum;

/**
 * Represents a login result that is returned from the LINE Platform.
 */
public class LineLoginResult implements Parcelable {
    public static final Creator<LineLoginResult> CREATOR = new Creator<LineLoginResult>() {
        @Override
        public LineLoginResult createFromParcel(final Parcel in) {
            return new LineLoginResult(in);
        }

        @Override
        public LineLoginResult[] newArray(final int size) {
            return new LineLoginResult[size];
        }
    };

    @NonNull
    private final LineApiResponseCode responseCode;
    @Nullable
    private final String nonce;
    @Nullable
    private final LineProfile lineProfile;
    @Nullable
    private final LineIdToken lineIdToken;
    @Nullable
    private final Boolean friendshipStatusChanged;
    @Nullable
    private final LineCredential lineCredential;
    @NonNull
    private final LineApiError errorData;

    private LineLoginResult(final Builder builder) {
        responseCode = builder.responseCode;
        nonce = builder.nonce;
        lineProfile = builder.lineProfile;
        lineIdToken = builder.lineIdToken;
        friendshipStatusChanged = builder.friendshipStatusChanged;
        lineCredential = builder.lineCredential;
        errorData = builder.errorData;
    }

    private LineLoginResult(@NonNull final Parcel in) {
        responseCode = readEnum(in, LineApiResponseCode.class);
        nonce = in.readString();
        lineProfile = in.readParcelable(LineProfile.class.getClassLoader());
        lineIdToken = in.readParcelable(LineIdToken.class.getClassLoader());
        friendshipStatusChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
        lineCredential = in.readParcelable(LineCredential.class.getClassLoader());
        errorData = in.readParcelable(LineApiError.class.getClassLoader());
    }

    public static LineLoginResult error(@NonNull final LineApiResponseCode resultCode,
                                        @NonNull final LineApiError errorData) {
        return new Builder()
                .responseCode(resultCode)
                .errorData(errorData)
                .build();
    }

    public static LineLoginResult error(@NonNull final LineApiResponse<?> apiResponse) {
        return error(apiResponse.getResponseCode(), apiResponse.getErrorData());
    }

    public static LineLoginResult internalError(@NonNull final LineApiError errorData) {
        return error(LineApiResponseCode.INTERNAL_ERROR, errorData);
    }

    public static LineLoginResult internalError(@NonNull final String errorMessage) {
        return internalError(new LineApiError(errorMessage));
    }

    public static LineLoginResult internalError(@NonNull final Exception e) {
        return internalError(new LineApiError(e));
    }

    public static LineLoginResult authenticationAgentError(@NonNull final LineApiError errorData) {
        return error(LineApiResponseCode.AUTHENTICATION_AGENT_ERROR, errorData);
    }

    public static LineLoginResult canceledError() {
        return error(LineApiResponseCode.CANCEL, LineApiError.DEFAULT);
    }

    /**
     * @hide
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @hide
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        writeEnum(dest, responseCode);
        dest.writeString(nonce);
        dest.writeParcelable(lineProfile, flags);
        dest.writeParcelable(lineIdToken, flags);
        dest.writeValue(friendshipStatusChanged);
        dest.writeParcelable(lineCredential, flags);
        dest.writeParcelable(errorData, flags);
    }

    /**
     * Checks whether the login was successful.
     *
     * @return True if the login is successful; false otherwise.
     */
    public boolean isSuccess() {
        return responseCode == LineApiResponseCode.SUCCESS;
    }

    /**
     * Gets the response code that the login returned.
     *
     * @return A {@link LineApiResponseCode} object with the response code that
     * indicates whether the login was successful or not.
     */
    @NonNull
    public LineApiResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * Gets the `nonce` value that used for performing LINE Login
     *
     * @return the `nonce` value that used for performing LINE Login
     */
    @Nullable
    public String getNonce() {
        return nonce;
    }

    /**
     * Gets the user's profile information.
     *
     * @return A {@link LineProfile} object with the user's LINE profile.
     */
    @Nullable
    public LineProfile getLineProfile() {
        return lineProfile;
    }

    /**
     * Gets the ID token that contains the user's information.
     *
     * @return A {@link LineIdToken} object with the user's information.
     */
    @Nullable
    public LineIdToken getLineIdToken() {
        return lineIdToken;
    }

    /**
     * Gets the friendship status of the user and the bot linked to your LINE Login channel.
     *
     * @return True if the user has added the bot as a friend and has not blocked the bot; false
     * otherwise.
     */
    @NonNull
    public Boolean getFriendshipStatusChanged() {
        if (friendshipStatusChanged == null) { return false; }

        return friendshipStatusChanged;
    }

    /**
     * Gets the user's credentials.
     *
     * @return A {@link LineCredential} object with the user's authentication credentials.
     */
    @Nullable
    public LineCredential getLineCredential() {
        return lineCredential;
    }

    /**
     * Gets information about a login error that has occurred. This method
     * should only be called if the login has failed.
     *
     * @return A {@link LineApiError} object that contains information about the
     * error if a login error has occurred. Contains a response of
     * <code>0</code> and a <code>null</code> string if no error occurs.
     */
    @NonNull
    public LineApiError getErrorData() {
        return errorData;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final LineLoginResult that = (LineLoginResult) o;

        if (responseCode != that.responseCode) { return false; }
        if (nonce != null ? !nonce.equals(that.nonce) : that.nonce != null) { return false; }
        if (lineProfile != null ? !lineProfile.equals(that.lineProfile) : that.lineProfile != null) {
            return false;
        }
        if (lineIdToken != null ? !lineIdToken.equals(that.lineIdToken) : that.lineIdToken != null) {
            return false;
        }
        if (friendshipStatusChanged != null ? !friendshipStatusChanged.equals(that.friendshipStatusChanged) :
            that.friendshipStatusChanged != null) { return false; }
        if (lineCredential != null ? !lineCredential.equals(that.lineCredential) :
            that.lineCredential != null) {
            return false;
        }
        return errorData.equals(that.errorData);
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = responseCode.hashCode();
        result = 31 * result + (nonce != null ? nonce.hashCode() : 0);
        result = 31 * result + (lineProfile != null ? lineProfile.hashCode() : 0);
        result = 31 * result + (lineIdToken != null ? lineIdToken.hashCode() : 0);
        result = 31 * result + (friendshipStatusChanged != null ? friendshipStatusChanged.hashCode() : 0);
        result = 31 * result + (lineCredential != null ? lineCredential.hashCode() : 0);
        result = 31 * result + errorData.hashCode();
        return result;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineLoginResult{" +
               "responseCode=" + responseCode +
               ", nonce='" + nonce + '\'' +
               ", lineProfile=" + lineProfile +
               ", lineIdToken=" + lineIdToken +
               ", friendshipStatusChanged=" + friendshipStatusChanged +
               ", lineCredential=" + lineCredential +
               ", errorData=" + errorData +
               '}';
    }

    /**
     * @hide
     */
    public static final class Builder {
        private LineApiResponseCode responseCode = LineApiResponseCode.SUCCESS;
        private String nonce;
        private LineProfile lineProfile;
        private LineIdToken lineIdToken;
        private Boolean friendshipStatusChanged;
        private LineCredential lineCredential;
        private LineApiError errorData = LineApiError.DEFAULT;

        public Builder() {}

        public Builder responseCode(final LineApiResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder nonce(final String nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder lineProfile(final LineProfile lineProfile) {
            this.lineProfile = lineProfile;
            return this;
        }

        public Builder lineIdToken(final LineIdToken lineIdToken) {
            this.lineIdToken = lineIdToken;
            return this;
        }

        public Builder friendshipStatusChanged(final Boolean friendshipStatusChanged) {
            this.friendshipStatusChanged = friendshipStatusChanged;
            return this;
        }

        public Builder lineCredential(final LineCredential lineCredential) {
            this.lineCredential = lineCredential;
            return this;
        }

        public Builder errorData(final LineApiError errorData) {
            this.errorData = errorData;
            return this;
        }

        public LineLoginResult build() {
            return new LineLoginResult(this);
        }
    }
}
