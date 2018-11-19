package com.linecorp.linesdk.internal;

import android.support.annotation.NonNull;

import com.linecorp.linesdk.BuildConfig;

/**
 * Immutable data class represents a pair of an one time identifier and a password.
 */
public class OneTimePassword {
    @NonNull
    private final String id;
    @NonNull
    private final String password;

    public OneTimePassword(@NonNull String id, @NonNull String password) {
        this.id = id;
        this.password = password;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    // Don't output the one time password because there is possibility to remain it on log.
    // Be careful not to remove this logic when you regenerate toString().
    @Override
    public String toString() {
        return "OneTimeIdAndPassword{" +
                "id='" + (BuildConfig.DEBUG ? id : "#####") + '\'' +
                ", password='" + (BuildConfig.DEBUG ? password : "#####") + '\'' +
                '}';
    }
}
