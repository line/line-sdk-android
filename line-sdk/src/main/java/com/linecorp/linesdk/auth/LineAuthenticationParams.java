package com.linecorp.linesdk.auth;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.Scope;

import java.util.List;
import java.util.Locale;

import static com.linecorp.linesdk.utils.ParcelUtils.readEnum;
import static com.linecorp.linesdk.utils.ParcelUtils.writeEnum;

/**
 * Represents a container to hold necessary parameters for performing LINE Login, including
 * permission scopes and the option to determine how to prompt the user to add a bot as a friend
 * during the login process.
 */
public class LineAuthenticationParams implements Parcelable {
    /**
     * @hide
     */
    public static final Creator<LineAuthenticationParams> CREATOR = new Creator<LineAuthenticationParams>() {
        @Override
        public LineAuthenticationParams createFromParcel(final Parcel in) {
            return new LineAuthenticationParams(in);
        }

        @Override
        public LineAuthenticationParams[] newArray(final int size) {
            return new LineAuthenticationParams[size];
        }
    };

    /**
     * REQUIRED. <br></br>
     * Permissions granted by the user. You can specify multiple scopes.
     */
    @NonNull
    private final List<Scope> scopes;

    /**
     * OPTIONAL. <br></br>
     * A string used to prevent replay attacks. This value is returned in an ID token.
     * If you don't specify a value for this parameter, LINE SDK will generate one.
     */
    @Nullable
    private final String nonce;

    /**
     * OPTIONAL. <br></br>
     * Displays an option to add a bot as a friend during login.
     */
    @Nullable
    private final BotPrompt botPrompt;

    /**
     * OPTIONAL. <br></br>
     * The language in which to display login pages. <br></br>
     * If not specified, login pages will use the language setting of user's web browser or LINE app.
     */
    @Nullable
    private final Locale uiLocale;

    private LineAuthenticationParams(final Builder builder) {
        scopes = builder.scopes;
        nonce = builder.nonce;
        botPrompt = builder.botPrompt;
        uiLocale = builder.uiLocale;
    }

    private LineAuthenticationParams(@NonNull final Parcel in) {
        scopes = Scope.convertToScopeList(in.createStringArrayList());
        nonce = in.readString();
        botPrompt = readEnum(in, BotPrompt.class);
        uiLocale = (Locale) in.readSerializable();
    }

    /**
     * @hide
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeStringList(Scope.convertToCodeList(scopes));
        dest.writeString(nonce);
        writeEnum(dest, botPrompt);
        dest.writeSerializable(uiLocale);
    }

    /**
     * @hide
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Gets a list of scopes.
     * @return The list of scopes.
     */
    @NonNull
    public List<Scope> getScopes() {
        return scopes;
    }

    /**
     * Gets a nonce (a string used to prevent replay attacks). This value is returned in an ID token.
     * @return nonce string
     */
    @Nullable
    public String getNonce() {
        return nonce;
    }

    /**
     * Gets the option to determine how to prompt the user to add a bot as a friend during the
     * login process.
     * @return The option to determine how to prompt the user to add a bot as a friend.
     */
    @Nullable
    public BotPrompt getBotPrompt() {
        return botPrompt;
    }

    /**
     * Gets the language in which login pages are displayed.
     * @return The language in which login pages are displayed.
     */
    @Nullable
    public Locale getUILocale() {
        return uiLocale;
    }

    /**
     * Represents an option to determine how to prompt the user to add a bot as a friend during the
     * login process.
     */
    public enum BotPrompt {
        /**
         * Includes an option to add a bot as a friend in the consent screen.
         */
        normal,

        /**
         * Opens a new screen to add a bot as a friend after the user agrees to the permissions in
         * the consent screen.
         */
        aggressive
    }

    /**
     * Represents a builder to construct LineAuthenticationParams objects.
     */
    public static final class Builder {
        private List<Scope> scopes;
        private String nonce;
        private BotPrompt botPrompt;
        private Locale uiLocale;

        public Builder() {}

        /**
         * Sets scopes to the builder.
         * @param val A list of scopes.
         * @return The builder itself.
         */
        public Builder scopes(final List<Scope> val) {
            scopes = val;
            return this;
        }

        /**
         * Sets nonce to the builder.
         * @param val A string used to prevent replay attacks. This value is returned in an ID token.
         * If you don't specify a value for this parameter, LINE SDK will generate one.
         * @return The builder itself.
         */
        public Builder nonce(final String val) {
            nonce = val;
            return this;
        }

        /**
         * Sets the option to determine how to prompt the user to add a bot as a friend.
         * @param val The option to determine how to prompt the user to add a bot as a friend.
         * @return The builder itself.
         */
        public Builder botPrompt(final BotPrompt val) {
            botPrompt = val;
            return this;
        }

        /**
         * Sets the language in which to display login pages. <br></br>
         * If not specified, login pages will use the language setting of user's web browser or LINE app.
         * @param val The language in which to display login pages.
         * @return The builder itself.
         */
        public Builder uiLocale(final Locale val) {
            uiLocale = val;
            return this;
        }

        /**
         * Builds LineAuthenticationParams objects.
         * @return The LineAuthenticationParams object.
         */
        public LineAuthenticationParams build() {
            return new LineAuthenticationParams(this);
        }
    }
}
