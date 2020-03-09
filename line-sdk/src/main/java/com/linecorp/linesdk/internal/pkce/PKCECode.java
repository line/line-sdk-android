package com.linecorp.linesdk.internal.pkce;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import androidx.annotation.NonNull;

import com.linecorp.linesdk.utils.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Proof Key for Code Exchange. <br></br>
 * for more details, please refer to: <a href="https://oauth.net/2/pkce/">RFC 7636: Proof Key for Code Exchange</a>
 */
public class PKCECode implements Parcelable {
    private static final int LENGTH_VERIFIER = 64;

    public static final Parcelable.Creator<PKCECode> CREATOR = new Parcelable.Creator<PKCECode>() {
        @Override
        public PKCECode createFromParcel(final Parcel in) {
            return new PKCECode(in);
        }

        @Override
        public PKCECode[] newArray(final int size) {
            return new PKCECode[size];
        }
    };

    @NonNull
    private final String verifier;

    @NonNull
    private final String challenge;

    private PKCECode(@NonNull final String verifier) {
        this.verifier = verifier;
        challenge = generateChallenge(verifier);
    }

    private PKCECode(final Parcel in) {
        verifier = in.readString();
        challenge = generateChallenge(verifier);
    }

    public static PKCECode newCode() {
        final String verifier = generateVerifier();
        return new PKCECode(verifier);
    }

    private static String generateVerifier() {
        return StringUtils.createRandomAlphaNumeric(LENGTH_VERIFIER);
    }

    @NonNull
    private static String generateChallenge(@NonNull final String verifier) {
        final MessageDigest messageDigest;
        try {
            // "code_challenge_method=plain" is not supported, always use "S256"
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update(verifier.getBytes());
        final byte[] digest = messageDigest.digest();

        return Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
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
    public void writeToParcel(final Parcel dest, int flags) {
        dest.writeString(verifier);
    }

    @NonNull
    public String getVerifier() {
        return verifier;
    }

    @NonNull
    public String getChallenge() {
        return challenge;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof PKCECode)) { return false; }

        final PKCECode pkceCode = (PKCECode) o;

        if (!verifier.equals(pkceCode.verifier)) { return false; }
        return challenge.equals(pkceCode.challenge);
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = verifier.hashCode();
        result = 31 * result + challenge.hashCode();
        return result;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "PKCECode{" +
               "verifier='" + verifier + '\'' +
               ", challenge='" + challenge + '\'' +
               '}';
    }
}
