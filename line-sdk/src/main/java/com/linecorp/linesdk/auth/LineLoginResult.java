package com.linecorp.linesdk.auth;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.linecorp.linesdk.LineApiError;
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
    public static final LineLoginResult CANCEL =
            new LineLoginResult(LineApiResponseCode.CANCEL, LineApiError.DEFAULT);

    public static final Parcelable.Creator<LineLoginResult> CREATOR = new Parcelable.Creator<LineLoginResult>() {
        @Override
        public LineLoginResult createFromParcel(Parcel in) {
            return new LineLoginResult(in);
        }

        @Override
        public LineLoginResult[] newArray(int size) {
            return new LineLoginResult[size];
        }
    };

    @NonNull
    private final LineApiResponseCode responseCode;
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

    public LineLoginResult(
            @NonNull LineProfile lineProfile,
            @Nullable LineIdToken lineIdToken,
            @Nullable Boolean friendshipStatusChanged,
            @NonNull LineCredential lineCredential) {
        this(LineApiResponseCode.SUCCESS,
             lineProfile,
             lineIdToken,
             friendshipStatusChanged,
             lineCredential,
             LineApiError.DEFAULT);
    }

    public LineLoginResult(
            @NonNull LineApiResponseCode resultCode, @NonNull LineApiError errorData) {
        this(resultCode,
             null /* lineProfile */,
             null /* lineIdToken */,
             null /* friendshipStatusChanged */,
             null /* lineCredential */,
             errorData);
    }

    @VisibleForTesting
    LineLoginResult(
            @NonNull LineApiResponseCode responseCode,
            @Nullable LineProfile lineProfile,
            @Nullable LineIdToken lineIdToken,
            @Nullable Boolean friendshipStatusChanged,
            @Nullable LineCredential lineCredential,
            @NonNull LineApiError errorData) {
        this.responseCode = responseCode;
        this.lineProfile = lineProfile;
        this.lineIdToken = lineIdToken;
        this.friendshipStatusChanged = friendshipStatusChanged;
        this.lineCredential = lineCredential;
        this.errorData = errorData;
    }

    private LineLoginResult(@NonNull Parcel in) {
        responseCode = readEnum(in, LineApiResponseCode.class);
        lineProfile = in.readParcelable(LineProfile.class.getClassLoader());
        lineIdToken = in.readParcelable(LineIdToken.class.getClassLoader());
        friendshipStatusChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
        lineCredential = in.readParcelable(LineCredential.class.getClassLoader());
        errorData = in.readParcelable(LineApiError.class.getClassLoader());
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
    public void writeToParcel(Parcel dest, int flags) {
        writeEnum(dest, responseCode);
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
        if (friendshipStatusChanged == null) return false;

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
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        LineLoginResult that = (LineLoginResult) o;

        if (responseCode != that.responseCode) { return false; }
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
               ", lineProfile=" + lineProfile +
               ", lineIdToken=" + lineIdToken +
               ", friendshipStatusChanged=" + friendshipStatusChanged +
               ", lineCredential=" + lineCredential +
               ", errorData=" + errorData +
               '}';
    }
}
