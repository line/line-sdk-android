package com.linecorp.linesdk;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import static com.linecorp.linesdk.utils.DebugUtils.hideIfNotDebug;

/**
 * Represents an access token that is used to call the Social API.
 */
public class LineAccessToken implements Parcelable {
    public static final Parcelable.Creator<LineAccessToken> CREATOR = new Parcelable.Creator<LineAccessToken>() {
        @Override
        public LineAccessToken createFromParcel(Parcel in) {
            return new LineAccessToken(in);
        }

        @Override
        public LineAccessToken[] newArray(int size) {
            return new LineAccessToken[size];
        }
    };

    @NonNull
    private final String accessToken;
    private final long expiresInMillis;
    private final long issuedClientTimeMillis;

    public LineAccessToken(
            @NonNull String accessToken,
            long expiresInMillis,
            long issuedClientTimeMillis) {
        this.accessToken = accessToken;
        this.expiresInMillis = expiresInMillis;
        this.issuedClientTimeMillis = issuedClientTimeMillis;
    }

    private LineAccessToken(@NonNull Parcel in) {
        accessToken = in.readString();
        expiresInMillis = in.readLong();
        issuedClientTimeMillis = in.readLong();
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
        dest.writeString(accessToken);
        dest.writeLong(expiresInMillis);
        dest.writeLong(issuedClientTimeMillis);
    }

    /**
     * Gets the string representation of the access token.
     *
     * @return The access token.
     */
    @NonNull
    public String getTokenString() {
        return accessToken;
    }

    /**
     * Gets the amount of time in milliseconds until the access token expires.
     *
     * @return The amount of time in milliseconds until the access token expires.
     */
    public long getExpiresInMillis() {
        return expiresInMillis;
    }

    /**
     * Gets the time in UNIX time when the access token information was last updated. This value is
     * updated upon login, when the token is refreshed, and when the token is verified.
     *
     * @return The time in UNIX time of when the access token information was last updated.
     */
    public long getIssuedClientTimeMillis() {
        return issuedClientTimeMillis;
    }

    /**
     * Gets the estimated time in UNIX time when the access token expires. The expiration time that
     * is returned is not exact because it is calculated using time values that are cached on the
     * client.
     *
     * @return The estimated time in UNIX time when the access token expires.
     */
    public long getEstimatedExpirationTimeMillis() {
        return getIssuedClientTimeMillis() + getExpiresInMillis();
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineAccessToken that = (LineAccessToken) o;

        if (expiresInMillis != that.expiresInMillis)
            return false;
        if (issuedClientTimeMillis != that.issuedClientTimeMillis) return false;
        return accessToken.equals(that.accessToken);
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = accessToken.hashCode();
        result = 31 * result + (int) (expiresInMillis ^ (expiresInMillis >>> 32));
        result = 31 * result + (int) (issuedClientTimeMillis ^ (issuedClientTimeMillis >>> 32));
        return result;
    }

    // Don't output the access token because there is possibility to remain it on log.
    // Be careful not to remove this logic when you regenerate toString().
    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineAccessToken{" +
               "accessToken='" + hideIfNotDebug(accessToken) + '\'' +
               ", expiresInMillis=" + expiresInMillis +
               ", issuedClientTimeMillis=" + issuedClientTimeMillis +
               '}';
    }
}
