package com.linecorp.linesdk.auth.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.linecorp.linesdk.Constants;

import java.util.StringTokenizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Class represents a version of LINE application.
 */
public class LineAppVersion {
    @Nullable
    public static LineAppVersion getLineAppVersion(@NonNull Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(Constants.LINE_APP_PACKAGE_NAME, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        String versionName = packageInfo.versionName;
        if (TextUtils.isEmpty(versionName)) {
            return null;
        }

        @SuppressWarnings("UseOfStringTokenizer")
        StringTokenizer stringTokenizer = new StringTokenizer(versionName, ".");
        try {
            return new LineAppVersion(
                    Integer.parseInt(stringTokenizer.nextToken()) /* major */,
                    Integer.parseInt(stringTokenizer.nextToken()) /* minor */,
                    Integer.parseInt(stringTokenizer.nextToken()) /* revision */);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private final int major;
    private final int minor;
    private final int revision;

    public LineAppVersion(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    public boolean isEqualOrGreaterThan(@Nullable LineAppVersion another) {
        if (another == null) {
            return false;
        }
        if (major != another.major) {
            return major >= another.major;
        }
        if (minor != another.minor) {
            return minor >= another.minor;
        }
        return revision >= another.revision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineAppVersion that = (LineAppVersion) o;

        if (major != that.major) return false;
        if (minor != that.minor) return false;
        if (revision != that.revision) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + revision;
        return result;
    }
}
