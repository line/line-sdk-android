package com.linecorp.android.security.encryption;

import androidx.annotation.Nullable;

/**
 * Exception represents encryption failure by such as falsification of cipher text.
 */
public class EncryptionException extends RuntimeException {
    public EncryptionException(@Nullable String message) {
        super(message);
    }

    public EncryptionException(@Nullable Throwable cause) {
        super(cause);
    }

    public EncryptionException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
