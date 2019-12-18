package com.linecorp.linesdk.internal;

import android.content.Context;
import androidx.annotation.NonNull;

import com.linecorp.android.security.encryption.StringCipher;

import java.util.concurrent.Executors;

/**
 * Class to hold StringCipher.
 * This class prevents to generate secret keys repeatedly because it is very slow.
 */
public class EncryptorHolder {
    // TODO: Change to be able to specify the iteration count by LINE SDK user.
    private static final int DEFAULT_ITERATION_COUNT = 5000;
    private static final String ENCRYPTION_SALT_SHARED_PREFERENCE_NAME
            = "com.linecorp.linesdk.sharedpreference.encryptionsalt";
    private static final StringCipher ENCRYPTOR = new StringCipher(
            ENCRYPTION_SALT_SHARED_PREFERENCE_NAME, DEFAULT_ITERATION_COUNT, true);
    private static volatile boolean s_isInitializationStarted = false;

    private EncryptorHolder() {
        // To prevent instantiation
    }

    public static void initializeOnWorkerThread(@NonNull Context context) {
        if (!s_isInitializationStarted) {
            s_isInitializationStarted = true;
            Executors.newSingleThreadExecutor().execute(
                    new EncryptorInitializationTask(context.getApplicationContext()));
        }
    }

    @NonNull
    public static StringCipher getEncryptor() {
        return ENCRYPTOR;
    }

    private static class EncryptorInitializationTask implements Runnable {
        @NonNull
        private final Context context;

        EncryptorInitializationTask(@NonNull Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            ENCRYPTOR.initialize(context);
        }
    }
}
