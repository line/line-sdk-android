package com.linecorp.android.security.encryption;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class to encrypt/decrypt string data.
 * This has the following features.
 * - derives a device-specific key
 * - randomizes encrypted data (uses AES-CBS with a random IV)
 * - provides integrity protection (applies HMAC to encrypted data)
 * <p>
 * This uses {@link SharedPreferences} to save the salt of encryption. Please specify a unique name
 * as the constructor parameter that represents Shared Preference name.
 * <p>
 * Either first access of {@link #encrypt(Context, String)}, {@link #decrypt(Context, String)} or
 * {@link #initialize(Context)} is very slow because there is secret key generation with PBKDF2.
 * We recommend that you initialize an instance of this class beforehand and cache it.
 */
@WorkerThread
public class StringCipher {
    // for PBKDF
    private static final int DEFAULT_ITERATIONS = 10000;

    private static final int SALT_SIZE_IN_BYTE = 16;
    private static final int IV_SIZE_IN_BYTE = 16;
    private static final int HMAC_SIZE_IN_BYTE = 32;
    private static final int AES_KEY_SIZE_IN_BIT = 256;
    private static final int HMAC_KEY_SIZE_IN_BIT = 256;

    private static final String SALT_SHARED_PREFERENCE_KEY = "salt";

    @NonNull
    private final Object syncObject = new Object();

    @NonNull
    private final String sharedPreferenceName;
    private final int pbkdf2IterationCount;
    private boolean isSerialIncludedInDevicePackageSpecificId;

    @NonNull
    private final SecureRandom secureRandom;
    @NonNull
    private final SecretKeyFactory keyFactory;
    @NonNull
    private final Cipher cipher;
    @NonNull
    private final Mac hmac;

    @Nullable
    private SecretKeys secretKeys;

    public StringCipher(@NonNull String sharedPreferenceName) {
        this(sharedPreferenceName, DEFAULT_ITERATIONS, false);
    }

    /**
    @param      sharedPreferenceName              shared pref name used to save salt
    @param      pbkdf2IterationCount              number of iteration used for encryption
    @param      isSerialIncludedInDevicePackageSpecificId
                     Indicates if we should also include Build.Serial as an identifier to generate
                     the device Id.
                     Note : This field should always be false as it is deprecated and
                     returns UNKNOWN in some cases from Android SDK >= 27
     */
    public StringCipher(
            @NonNull String sharedPreferenceName,
            int pbkdf2IterationCount,
            boolean isSerialIncludedInDevicePackageSpecificId) {
        this.sharedPreferenceName = sharedPreferenceName;
        this.pbkdf2IterationCount = pbkdf2IterationCount;
        this.isSerialIncludedInDevicePackageSpecificId = isSerialIncludedInDevicePackageSpecificId;
        // should be available on all Android 15+ devices
        // hard fail if not so
        try {
            secureRandom = new SecureRandom();
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            hmac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize(@NonNull Context context) {
        synchronized (syncObject) {
            if (secretKeys == null) {
                secretKeys = getSecretKeys(context);
            }
        }
    }

    @NonNull
    public String encrypt(@NonNull Context context, @NonNull String plainText) {
        synchronized (syncObject) {
            initialize(context);
            try {
                // generate random IV
                byte[] iv = new byte[cipher.getBlockSize()];
                secureRandom.nextBytes(iv);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                // encrypt first
                cipher.init(Cipher.ENCRYPT_MODE, secretKeys.encryptionKey, ivSpec);
                byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

                // result is IV || ciphertext || MAC
                byte[] result = new byte[iv.length + cipherText.length + HMAC_SIZE_IN_BYTE];
                int idx = 0;
                System.arraycopy(iv, 0, result, idx, iv.length);
                idx += iv.length;
                System.arraycopy(cipherText, 0, result, idx, cipherText.length);

                // calculate MAC of IV || cipher text
                hmac.init(secretKeys.integrityKey);
                hmac.update(result, 0, iv.length + cipherText.length);
                byte[] mac = hmac.doFinal();

                idx += cipherText.length;
                System.arraycopy(mac, 0, result, idx, mac.length);

                return Base64.encodeToString(result, Base64.DEFAULT);
            } catch (BadPaddingException e) {
                throw new EncryptionException(e);
            } catch (UnsupportedEncodingException
                    | InvalidKeyException
                    | IllegalBlockSizeException
                    | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NonNull
    public String decrypt(@NonNull Context context, @NonNull String b64CipherText) {
        synchronized (syncObject) {
            initialize(context);
            try {
                byte[] cipherTextAndMac = Base64.decode(b64CipherText, Base64.DEFAULT);
                // get mac, last 32 bytes
                int idx = cipherTextAndMac.length - HMAC_SIZE_IN_BYTE;
                byte[] mac = Arrays.copyOfRange(cipherTextAndMac, idx, cipherTextAndMac.length);
                // calculate MAC again
                hmac.init(secretKeys.integrityKey);
                hmac.update(cipherTextAndMac, 0, cipherTextAndMac.length - HMAC_SIZE_IN_BYTE);
                byte[] calcMac = hmac.doFinal();

                // check if MAC matches
                if (!MessageDigest.isEqual(calcMac, mac)) {
                    throw new EncryptionException("Cipher text has been tampered with.");
                }

                // only decrypt if MAC checks out
                IvParameterSpec ivSpec = new IvParameterSpec(cipherTextAndMac, 0, IV_SIZE_IN_BYTE);
                cipher.init(Cipher.DECRYPT_MODE, secretKeys.encryptionKey, ivSpec);
                byte[] plaintextBytes = cipher.doFinal(
                        cipherTextAndMac,
                        IV_SIZE_IN_BYTE,
                        cipherTextAndMac.length - IV_SIZE_IN_BYTE - HMAC_SIZE_IN_BYTE);

                return new String(plaintextBytes, "UTF-8");
            } catch (BadPaddingException e) {
                throw new EncryptionException(e);
            } catch (UnsupportedEncodingException
                    | InvalidKeyException
                    | IllegalBlockSizeException
                    | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NonNull
    private SecretKeys getSecretKeys(@NonNull Context context) {
        String deviceId = generateDevicePackageSpecificId(context);
        byte[] salt = getSalt(context);
        KeySpec spec = new PBEKeySpec(
                deviceId.toCharArray(),
                salt,
                pbkdf2IterationCount,
                AES_KEY_SIZE_IN_BIT + HMAC_KEY_SIZE_IN_BIT);

        byte[] keyBytes;
        try {
            keyBytes = keyFactory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        SecretKey encryptionKey = new SecretKeySpec(
                Arrays.copyOfRange(keyBytes, 0, AES_KEY_SIZE_IN_BIT / 8), "AES");
        SecretKey integrityKey = new SecretKeySpec(
                Arrays.copyOfRange(keyBytes, HMAC_KEY_SIZE_IN_BIT / 8, keyBytes.length), "HmacSHA256");
        return new SecretKeys(encryptionKey, integrityKey);
    }

    @NonNull
    private String generateDevicePackageSpecificId(@NonNull Context context) {
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);

        String serial = isSerialIncludedInDevicePackageSpecificId ? Build.SERIAL : "";

        return Build.MODEL + Build.MANUFACTURER + serial + androidId + context.getPackageName();
    }

    @NonNull
    private byte[] getSalt(@NonNull Context context) {
        SharedPreferences sharedPrefs
                = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);

        String savedSalt = sharedPrefs.getString(SALT_SHARED_PREFERENCE_KEY, null /* default */);
        // return saved salt if exists
        if (!TextUtils.isEmpty(savedSalt)) {
            return Base64.decode(savedSalt, Base64.DEFAULT);
        }
        // otherwise generate, save, and return
        byte[] salt = new byte[SALT_SIZE_IN_BYTE];
        secureRandom.nextBytes(salt);
        sharedPrefs
                .edit()
                .putString(SALT_SHARED_PREFERENCE_KEY, Base64.encodeToString(salt, Base64.DEFAULT))
                .apply();
        return salt;
    }

    private static class SecretKeys {
        @NonNull
        private final SecretKey encryptionKey;
        @NonNull
        private final SecretKey integrityKey;

        SecretKeys(
                @NonNull SecretKey encryptionKey,
                @NonNull SecretKey integrityKey) {
            this.encryptionKey = encryptionKey;
            this.integrityKey = integrityKey;
        }
    }
}
