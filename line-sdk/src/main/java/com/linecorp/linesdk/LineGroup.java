package com.linecorp.linesdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @hide
 * This is a class to represent a LINE group information.
 */
public class LineGroup implements Parcelable {
    public static final Creator<LineGroup> CREATOR = new Creator<LineGroup>() {
        @Override
        public LineGroup createFromParcel(Parcel in) {
            return new LineGroup(in);
        }

        @Override
        public LineGroup[] newArray(int size) {
            return new LineGroup[size];
        }
    };

    @NonNull
    private final String groupId;
    @NonNull
    private final String groupName;
    @Nullable
    private final Uri pictureUrl;

    /**
     * constructor for creating a LINE group instance
     * @param groupId group's ID
     * @param groupName group's name
     * @param pictureUrl group's image URL
     */
    public LineGroup(
            @NonNull String groupId,
            @NonNull String groupName,
            @Nullable Uri pictureUrl) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.pictureUrl = pictureUrl;
    }

    private LineGroup(@NonNull Parcel in) {
        groupId = in.readString();
        groupName = in.readString();
        pictureUrl = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeString(groupName);
        dest.writeParcelable(pictureUrl, flags);
    }

    /**
     * Gets group's name.
     * @return
     */
    @NonNull
    public String getGroupName() {
        return groupName;
    }

    /**
     * Gets group's ID.
     * @return
     */
    @NonNull
    public String getGroupId() {
        return groupId;
    }

    /**
     * Gets group's image URL.
     * @return
     */
    @Nullable
    public Uri getPictureUrl() {
        return pictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineGroup that = (LineGroup) o;

        if (!groupId.equals(that.groupId)) return false;
        if (!groupName.equals(that.groupName)) return false;
        if (pictureUrl != null ? !pictureUrl.equals(that.pictureUrl) : that.pictureUrl != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + groupName.hashCode();
        result = 31 * result + (pictureUrl != null ? pictureUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LineProfile{" +
                "groupName='" + groupName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }
}
