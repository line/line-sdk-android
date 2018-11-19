package com.linecorp.linesdk.auth;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.linecorp.linesdk.Scope;

import java.util.List;

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
     * Displays an option to add a bot as a friend during login.
     */
    @Nullable
    private final BotPrompt botPrompt;

    private LineAuthenticationParams(final Builder builder) {
        scopes = builder.scopes;
        botPrompt = builder.botPrompt;
    }

    private LineAuthenticationParams(@NonNull final Parcel in) {
        scopes = Scope.convertToScopeList(in.createStringArrayList());
        botPrompt = readEnum(in, BotPrompt.class);
    }

    /**
     * @hide
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeStringList(Scope.convertToCodeList(scopes));
        writeEnum(dest, botPrompt);
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
     * Gets the option to determine how to prompt the user to add a bot as a friend during the
     * login process.
     * @return The option to determine how to prompt the user to add a bot as a friend.
     */
    @Nullable
    public BotPrompt getBotPrompt() {
        return botPrompt;
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
        private BotPrompt botPrompt;

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
         * Sets the option to determine how to prompt the user to add a bot as a friend.
         * @param val The option to determine how to prompt the user to add a bot as a friend.
         * @return The builder itself.
         */
        public Builder botPrompt(final BotPrompt val) {
            botPrompt = val;
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
