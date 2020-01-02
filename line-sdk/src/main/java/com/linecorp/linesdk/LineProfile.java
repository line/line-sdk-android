package com.linecorp.linesdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Represents a user's LINE profile in the Social API.
 */
public class LineProfile implements Parcelable {
    public static final Parcelable.Creator<LineProfile> CREATOR = new Parcelable.Creator<LineProfile>() {
        @Override
        public LineProfile createFromParcel(Parcel in) {
            return new LineProfile(in);
        }

        @Override
        public LineProfile[] newArray(int size) {
            return new LineProfile[size];
        }
    };

    @NonNull
    private final String userId;
    @NonNull
    private final String displayName;
    @Nullable
    private final Uri pictureUrl;
    @Nullable
    private final String statusMessage;

    /**
     * Constructs a new {@link LineProfile} instance.
     * @param userId User's user ID.
     * @param displayName User's display name.
     * @param pictureUrl User's profile image URL.
     * @param statusMessage User's status message.
     */
    public LineProfile(
            @NonNull String userId,
            @NonNull String displayName,
            @Nullable Uri pictureUrl,
            @Nullable String statusMessage) {
        this.userId = userId;
        this.displayName = displayName;
        this.pictureUrl = pictureUrl;
        this.statusMessage = statusMessage;
    }

    protected LineProfile(@NonNull Parcel in) {
        userId = in.readString();
        displayName = in.readString();
        pictureUrl = in.readParcelable(Uri.class.getClassLoader());
        statusMessage = in.readString();
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
        dest.writeString(userId);
        dest.writeString(displayName);
        dest.writeParcelable(pictureUrl, flags);
        dest.writeString(statusMessage);
    }

    /**
     * Gets the user's display name.
     * @return The user's display name.
     */
    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the user's user ID.
     * @return The user's user ID.
     */
    @NonNull
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the user's profile image URL.
     * @return The user's profile image URL.
     */
    @Nullable
    public Uri getPictureUrl() {
        return pictureUrl;
    }

    /**
     * Gets the user's status message.
     * @return The user's status message.
     */
    @Nullable
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineProfile that = (LineProfile) o;

        if (!userId.equals(that.userId)) return false;
        if (!displayName.equals(that.displayName)) return false;
        if (pictureUrl != null ? !pictureUrl.equals(that.pictureUrl) : that.pictureUrl != null)
            return false;
        return statusMessage != null ? statusMessage.equals(that.statusMessage) : that.statusMessage == null;

    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + displayName.hashCode();
        result = 31 * result + (pictureUrl != null ? pictureUrl.hashCode() : 0);
        result = 31 * result + (statusMessage != null ? statusMessage.hashCode() : 0);
        return result;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineProfile{" +
               "userId='" + userId + '\'' +
               ", displayName='" + displayName + '\'' +
               ", pictureUrl=" + pictureUrl +
               ", statusMessage='" + statusMessage + '\'' +
               '}';
    }
}
