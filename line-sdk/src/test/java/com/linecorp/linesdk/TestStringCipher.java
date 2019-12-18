package com.linecorp.linesdk;

import android.content.Context;
import androidx.annotation.NonNull;

import com.linecorp.android.security.encryption.StringCipher;

/**
 * Test implementation of {@link StringCipher}.
 */
public class TestStringCipher extends StringCipher {
    private static final String SHARED_PREFERENCE_NAME = "testSharedPreferenceForEncryptionSalt";
    private static final String ENCRYPTED_DATA_SUFFIX = "-encrypted";

    public TestStringCipher() {
        super(SHARED_PREFERENCE_NAME);
    }

    @Override
    public void initialize(@NonNull Context context) {
        // Do nothing
    }

    @NonNull
    @Override
    public String encrypt(@NonNull Context context, @NonNull String plainText) {
        return plainText + ENCRYPTED_DATA_SUFFIX;
    }

    @NonNull
    @Override
    public String decrypt(@NonNull Context context, @NonNull String b64CipherText) {
        return b64CipherText.contains(ENCRYPTED_DATA_SUFFIX)
                ? b64CipherText.substring(0, b64CipherText.length() - ENCRYPTED_DATA_SUFFIX.length())
                : "";
    }
}
