package com.linecorp.linesdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.linecorp.android.security.encryption.EncryptionException;
import com.linecorp.android.security.encryption.StringCipher;
import com.linecorp.linesdk.utils.ObjectUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

/**
 * Class to cache {@link InternalAccessToken}.
 */
public class AccessTokenCache {
    private static final String SHARED_PREFERENCE_KEY_PREFIX = "com.linecorp.linesdk.accesstoken.";

    private static final String DATA_KEY_ACCESS_TOKEN = "accessToken";
    private static final String DATA_KEY_EXPIRES_IN_MILLIS = "expiresIn";
    private static final String DATA_KEY_ISSUED_CLIENT_TIME_MILLIS = "issuedClientTime";
    private static final String DATA_KEY_REFRESH_TOKEN = "refreshToken";

    private static final long NO_DATA = -1;

    @NonNull
    private final Context context;
    @NonNull
    private final String sharedPreferenceKey;
    @NonNull
    private final StringCipher encryptor;

    public AccessTokenCache(@NonNull Context context, @NonNull String channelId) {
        this(context.getApplicationContext(), channelId, EncryptorHolder.getEncryptor());
    }

    @VisibleForTesting
    public AccessTokenCache(
            @NonNull Context context,
            @NonNull String  channelId,
            @NonNull StringCipher encryptor) {
        this.context = context;
        sharedPreferenceKey = SHARED_PREFERENCE_KEY_PREFIX + channelId;
        this.encryptor = encryptor;
    }

    public void clear() {
        context.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    public void saveAccessToken(@NonNull InternalAccessToken accessToken) {
        context.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
                .edit()
                .putString(DATA_KEY_ACCESS_TOKEN,
                        encryptString(accessToken.getAccessToken()))
                .putString(DATA_KEY_EXPIRES_IN_MILLIS,
                        encryptLong(accessToken.getExpiresInMillis()))
                .putString(DATA_KEY_ISSUED_CLIENT_TIME_MILLIS,
                        encryptLong(accessToken.getIssuedClientTimeMillis()))
                .putString(DATA_KEY_REFRESH_TOKEN,
                        encryptString(accessToken.getRefreshToken()))
                .apply();
    }

    @Nullable
    public InternalAccessToken getAccessToken() {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE);
        String accessToken;
        long expiresIn;
        long issuedClientTime;
        try {
            accessToken = decryptToString(sharedPreferences.getString(DATA_KEY_ACCESS_TOKEN, null /* default */));
            expiresIn = decryptToLong(sharedPreferences.getString(DATA_KEY_EXPIRES_IN_MILLIS, null /* default */));
            issuedClientTime = decryptToLong(sharedPreferences.getString(DATA_KEY_ISSUED_CLIENT_TIME_MILLIS, null /* default */));
        } catch (EncryptionException exception) {
            clear();
            throw exception;
        }

        if (TextUtils.isEmpty(accessToken)
                || expiresIn == NO_DATA
                || issuedClientTime == NO_DATA) {
            return null;
        }

        String refreshToken = decryptToString(sharedPreferences.getString(DATA_KEY_REFRESH_TOKEN, null /* default */));
        refreshToken = ObjectUtils.defaultIfNull(refreshToken, "");
        return new InternalAccessToken(accessToken, expiresIn, issuedClientTime, refreshToken);
    }

    @NonNull
    private String encryptString(@NonNull String target) {
        return encryptor.encrypt(context, target);
    }

    @NonNull
    private String encryptLong(long target) {
        return encryptor.encrypt(context, String.valueOf(target));
    }

    @Nullable
    private String decryptToString(@Nullable String target) {
        if (target == null) {
            return null;
        }
        return encryptor.decrypt(context, target);
    }

    private long decryptToLong(@Nullable String target) {
        if (target == null) {
            return NO_DATA;
        }
        String decryptedValue = encryptor.decrypt(context, target);
        try {
            return Long.valueOf(decryptedValue);
        } catch (NumberFormatException e) {
            return NO_DATA;
        }
    }
}
