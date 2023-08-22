package com.linecorp.linesdk;

import android.content.Context;
import androidx.annotation.NonNull;

import com.linecorp.android.security.encryption.StringCipherDeprecated;

/**
 * Test implementation of {@link StringCipherDeprecated}.
 */
public class TestStringCipherDeprecated extends StringCipherDeprecated {
    private static final String SHARED_PREFERENCE_NAME = "testSharedPreferenceForEncryptionSalt";
    private static final String ENCRYPTED_DATA_SUFFIX = "-encrypted";

    public TestStringCipherDeprecated() {
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
