package com.linecorp.linesdktest.settings;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

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

    @Nullable
    private final Locale uiLocale;

    public TestSetting(@NonNull String channelId, @Nullable Locale uiLocale) {
        this.channelId = channelId;
        this.uiLocale = uiLocale;
    }

    private TestSetting(@NonNull Parcel in) {
        channelId = in.readString();
        uiLocale = (Locale) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelId);
        dest.writeSerializable(uiLocale);
    }

    @NonNull
    public String getChannelId() {
        return channelId;
    }

    @Nullable
    public Locale getUILocale() {
        return uiLocale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        TestSetting that = (TestSetting) o;

        if (!channelId.equals(that.channelId)) { return false; }
        return uiLocale != null ? uiLocale.equals(that.uiLocale) : that.uiLocale == null;
    }

    @Override
    public int hashCode() {
        int result = channelId.hashCode();
        result = 31 * result + (uiLocale != null ? uiLocale.hashCode() : 0);
        return result;
    }
}
