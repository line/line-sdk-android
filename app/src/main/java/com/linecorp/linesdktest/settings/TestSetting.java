package com.linecorp.linesdktest.settings;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class TestSetting implements Parcelable {
    public static final Parcelable.Creator<TestSetting> CREATOR = new Parcelable.Creator<TestSetting>() {
        @Override
        public TestSetting createFromParcel(Parcel in) {
            return new TestSetting(in);
        }

        @Override
        public TestSetting[] newArray(int size) {
            return new TestSetting[size];
        }
    };

    @NonNull
    private final String channelId;

    public TestSetting(@NonNull String channelId) {
        this.channelId = channelId;
    }

    private TestSetting(@NonNull Parcel in) {
        channelId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelId);
    }

    @NonNull
    public String getChannelId() {
        return channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        TestSetting that = (TestSetting) o;

        return channelId.equals(that.channelId);
    }

    @Override
    public int hashCode() {
        return channelId.hashCode();
    }
}
