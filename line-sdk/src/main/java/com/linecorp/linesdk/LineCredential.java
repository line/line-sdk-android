package com.linecorp.linesdk;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.linecorp.linesdk.utils.DebugUtils.hideIfNotDebug;

/**
 * Represents credentials that are used to grant access to the Social API.
 */
public class LineCredential implements Parcelable {
    public static final Parcelable.Creator<LineCredential> CREATOR = new Parcelable.Creator<LineCredential>() {
        @Override
        public LineCredential createFromParcel(Parcel in) {
            return new LineCredential(in);
        }

        @Override
        public LineCredential[] newArray(int size) {
            return new LineCredential[size];
        }
    };

    @NonNull
    private final LineAccessToken accessToken;
    @NonNull
    private final List<Scope> scopes;

    public LineCredential(
            @NonNull LineAccessToken accessToken, @NonNull List<Scope> scopes) {
        this.accessToken = accessToken;
        this.scopes = scopes;
    }

    private LineCredential(@NonNull Parcel in) {
        accessToken = in.readParcelable(LineAccessToken.class.getClassLoader());
        List<String> modifiableScopes = new ArrayList<>(8);
        in.readStringList(modifiableScopes);
        scopes = Scope.convertToScopeList(modifiableScopes);
    }

    /**
     * @hide
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(accessToken, flags);
        dest.writeStringList(Scope.convertToCodeList(scopes));
    }

    /**
     * @hide
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Gets the access token.
     *
     * @return The {@link LineAccessToken} object that contains the access token.
     */
    @NonNull
    public LineAccessToken getAccessToken() {
        return accessToken;
    }

    /**
     * Gets a list of permissions that the access token holds.
     *
     * @return A string list of permission codes that are associated with the
     * access token.
     * @see Scope
     */
    @NonNull
    public List<Scope> getScopes() {
        return scopes;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        LineCredential that = (LineCredential) o;

        if (!accessToken.equals(that.accessToken)) { return false; }
        return scopes.equals(that.scopes);
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = accessToken.hashCode();
        result = 31 * result + scopes.hashCode();
        return result;
    }

    // Don't output the access token because there is possibility to remain it on log.
    // Be careful not to remove this logic when you regenerate toString().
    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineCredential{" +
               "accessToken=" + hideIfNotDebug(accessToken) +
               ", scopes=" + scopes +
               '}';
    }
}
