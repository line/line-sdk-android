package com.linecorp.linesdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Represents a friend's LINE profile in the Social API.
 */
public class LineFriendProfile extends LineProfile {
    public static final Parcelable.Creator<LineFriendProfile> CREATOR =
            new Parcelable.Creator<LineFriendProfile>() {
                @Override
                public LineFriendProfile createFromParcel(final Parcel in) {
                    return new LineFriendProfile(in);
                }

                @Override
                public LineFriendProfile[] newArray(final int size) {
                    return new LineFriendProfile[size];
                }
            };

    /**
     * Friend's nickname which changed by owner user
     */
    @Nullable
    private final String overriddenDisplayName;

    /**
     * Constructs a new {@link LineFriendProfile} instance.
     * @param userId Friend's user ID.
     * @param displayName Friend's display name.
     * @param pictureUrl Friend's profile image URL.
     * @param statusMessage Friend's status message.
     * @param overriddenDisplayName Friend's nickname which changed by owner user
     */
    public LineFriendProfile(@NonNull final String userId,
                             @NonNull final String displayName,
                             @Nullable final Uri pictureUrl,
                             @Nullable final String statusMessage,
                             @NonNull final String overriddenDisplayName) {
        super(userId, displayName, pictureUrl, statusMessage);
        this.overriddenDisplayName = overriddenDisplayName;
    }

    protected LineFriendProfile(@NonNull final Parcel in) {
        super(in);
        overriddenDisplayName = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(overriddenDisplayName);
    }

    /**
     * get the friend's nickname which changed by owner user
     * @return The friend's nickname which changed by owner user
     */
    @Nullable
    public String getOverriddenDisplayName() {
        return overriddenDisplayName;
    }

    /**
     * get the friend's nickname which changed by owner user, or the original display name.
     * @return The friend's nickname which changed by owner user, if not available, return the original display name
     */
    @NonNull
    public String getAvailableDisplayName() {
        return !TextUtils.isEmpty(overriddenDisplayName)
               ? overriddenDisplayName
               : getDisplayName();
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof LineFriendProfile)) { return false; }
        if (!super.equals(o)) { return false; }

        final LineFriendProfile that = (LineFriendProfile) o;

        return overriddenDisplayName != null ? overriddenDisplayName.equals(that.overriddenDisplayName) :
               that.overriddenDisplayName == null;
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (overriddenDisplayName != null ? overriddenDisplayName.hashCode() : 0);
        return result;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineFriendProfile{" +
               "userId='" + getUserId() + '\'' +
               ", displayName='" + getDisplayName() + '\'' +
               ", pictureUrl=" + getPictureUrl() +
               ", statusMessage='" + getStatusMessage() + '\'' +
               ", overriddenDisplayName='" + overriddenDisplayName + '\'' +
               '}';
    }
}
