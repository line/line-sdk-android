package com.linecorp.linesdk.internal.security.encryption

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import android.security.keystore.KeyProperties.PURPOSE_VERIFY
import android.util.Base64
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES cipher by AndroidKeyStore with software fallback.
 *
 * When Android KeyStore fails (e.g. SECURE_HW_COMMUNICATION_FAILED on some devices),
 * falls back to PBKDF2-derived software keys to ensure the encryption flow continues.
 *
 * @see <a href="https://issuetracker.google.com/issues/229764028">Android KeyStore issues</a>
 */
class StringAesCipher : StringCipher {

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEY_STORE).also {
            it.load(null)
        }
    }

    private lateinit var hmac: Mac

    @Volatile
    private var encryptionMode: EncryptionMode = EncryptionMode.UNINITIALIZED

    private var softwareAesKey: SecretKey? = null
    private var softwareIntegrityKey: SecretKey? = null

    override fun initialize(context: Context) {
        if (::hmac.isInitialized && encryptionMode != EncryptionMode.UNINITIALIZED) {
            return
        }

        synchronized(this) {
            if (::hmac.isInitialized && encryptionMode != EncryptionMode.UNINITIALIZED) {
                return
            }

            val prefs = getEncryptionPreferences(context)
            val savedMode = prefs.getString(KEY_ENCRYPTION_MODE, null)

            when (savedMode) {
                MODE_SOFTWARE -> {
                    initializeWithSoftwareKeys(context, prefs)
                    return
                }
                else -> {
                    // MODE_KEYSTORE or first run: try KeyStore with retry
                    if (initializeWithKeyStore(context, prefs)) {
                        return
                    }
                    // KeyStore failed: fallback to software
                    initializeWithSoftwareKeys(context, prefs)
                }
            }
        }
    }

    /**
     * Try to initialize with Android KeyStore. Includes retry and StrongBox avoidance.
     * @return true if successful, false if should fallback to software
     */
    private fun initializeWithKeyStore(context: Context, prefs: SharedPreferences): Boolean {
        val maxRetries = 3
        val retryDelayMs = 100L

        repeat(maxRetries) { attempt ->
            try {
                val aesKey = getAesSecretKeyFromKeyStore()
                val integrityKey = getIntegrityKeyFromKeyStore()

                hmac = Mac.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256).apply {
                    init(integrityKey)
                }
                encryptionMode = EncryptionMode.KEYSTORE
                prefs.edit().putString(KEY_ENCRYPTION_MODE, MODE_KEYSTORE).apply()
                return true
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) {
                    return false
                }
                try {
                    Thread.sleep(retryDelayMs)
                } catch (ie: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return false
                }
            }
        }
        return false
    }

    private fun initializeWithSoftwareKeys(context: Context, prefs: SharedPreferences) {
        val salt = getOrCreateSalt(prefs)
        val (aesKey, integrityKey) = deriveSoftwareKeys(context, salt)

        softwareAesKey = aesKey
        softwareIntegrityKey = integrityKey

        hmac = Mac.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256).apply {
            init(integrityKey)
        }
        encryptionMode = EncryptionMode.SOFTWARE
        prefs.edit().putString(KEY_ENCRYPTION_MODE, MODE_SOFTWARE).apply()
    }

    override fun encrypt(context: Context, plainText: String): String {
        synchronized(this) {
            initialize(context)

            val secretKey = when (encryptionMode) {
                EncryptionMode.KEYSTORE -> getAesSecretKeyFromKeyStore()
                EncryptionMode.SOFTWARE -> softwareAesKey
                    ?: throw EncryptionException("Software key not initialized")
                EncryptionMode.UNINITIALIZED -> throw EncryptionException("Encryptor not initialized")
            }

            try {
                val cipher = Cipher.getInstance(TRANSFORMATION_FORMAT).apply {
                    init(Cipher.ENCRYPT_MODE, secretKey)
                }
                val encryptedData: ByteArray = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

                return CipherData(
                    encryptedData = encryptedData,
                    initialVector = cipher.iv!!,
                    hmacValue = hmac.calculateHmacValue(encryptedData, cipher.iv!!)
                ).encodeToBase64String()
            } catch (e: Exception) {
                throw EncryptionException("Failed to encrypt", e)
            }
        }
    }

    override fun decrypt(context: Context, cipherText: String): String {
        synchronized(this) {
            initialize(context)

            val secretKey = when (encryptionMode) {
                EncryptionMode.KEYSTORE -> getAesSecretKeyFromKeyStore()
                EncryptionMode.SOFTWARE -> softwareAesKey
                    ?: throw EncryptionException("Software key not initialized")
                EncryptionMode.UNINITIALIZED -> throw EncryptionException("Encryptor not initialized")
            }

            try {
                val cipherData = CipherData.decodeFromBase64String(cipherText)

                cipherData.verifyHmacValue(hmac)

                val ivSpec = IvParameterSpec(cipherData.initialVector)

                return Cipher.getInstance(TRANSFORMATION_FORMAT)
                    .apply { init(Cipher.DECRYPT_MODE, secretKey, ivSpec) }
                    .run { doFinal(cipherData.encryptedData) }
                    .let { String(it, Charsets.UTF_8) }
            } catch (e: Exception) {
                throw EncryptionException("Failed to decrypt", e)
            }
        }
    }

    private fun getAesSecretKeyFromKeyStore(): SecretKey {
        return if (keyStore.containsAlias(AES_KEY_ALIAS)) {
            val secretKeyEntry =
                keyStore.getEntry(AES_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            secretKeyEntry.secretKey
        } else {
            createAesKey()
        }
    }

    private fun getIntegrityKeyFromKeyStore(): SecretKey {
        return if (keyStore.containsAlias(INTEGRITY_KEY_ALIAS)) {
            val secretKeyEntry =
                keyStore.getEntry(INTEGRITY_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            secretKeyEntry.secretKey
        } else {
            createIntegrityKey()
        }
    }

    /**
     * Create a new AES key in the Android KeyStore.
     * Uses setIsStrongBoxBacked(false) on API 28+ to avoid StrongBox communication failures.
     */
    private fun createAesKey(): SecretKey {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

        val builder = KeyGenParameterSpec.Builder(
            AES_KEY_ALIAS,
            PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE_IN_BIT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setIsStrongBoxBacked(false)
        }

        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    /**
     * Create HMAC key in Android KeyStore.
     * Uses setIsStrongBoxBacked(false) on API 28+ to avoid StrongBox communication failures.
     */
    private fun createIntegrityKey(): SecretKey {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEY_STORE)

        val builder = KeyGenParameterSpec.Builder(
            INTEGRITY_KEY_ALIAS,
            PURPOSE_SIGN or PURPOSE_VERIFY
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setIsStrongBoxBacked(false)
        }

        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    private fun getEncryptionPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_ENCRYPTION, Context.MODE_PRIVATE)
    }

    private fun getOrCreateSalt(prefs: SharedPreferences): ByteArray {
        val savedSalt = prefs.getString(KEY_SALT, null)
        if (!savedSalt.isNullOrEmpty()) {
            return Base64.decode(savedSalt, Base64.NO_WRAP)
        }
        val salt = ByteArray(SALT_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
        prefs.edit().putString(KEY_SALT, Base64.encodeToString(salt, Base64.NO_WRAP)).apply()
        return salt
    }

    @SuppressLint("HardwareIds")
    private fun generateDevicePackageSpecificId(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""
        return "${Build.MODEL}${Build.MANUFACTURER}$androidId${context.packageName}"
    }

    private fun deriveSoftwareKeys(context: Context, salt: ByteArray): Pair<SecretKey, SecretKey> {
        val deviceId = generateDevicePackageSpecificId(context)
        val spec: KeySpec = PBEKeySpec(
            deviceId.toCharArray(),
            salt,
            PBKDF2_ITERATIONS,
            AES_KEY_SIZE_BITS + HMAC_KEY_SIZE_BITS
        )

        val keyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val keyBytes = keyFactory.generateSecret(spec).encoded!!

        val aesKey = SecretKeySpec(
            keyBytes.copyOfRange(0, AES_KEY_SIZE_BITS / 8),
            "AES"
        )
        val integrityKey = SecretKeySpec(
            keyBytes.copyOfRange(AES_KEY_SIZE_BITS / 8, keyBytes.size),
            "HmacSHA256"
        )

        return aesKey to integrityKey
    }

    private fun Mac.calculateHmacValue(
        encryptedData: ByteArray,
        initialVector: ByteArray
    ): ByteArray = doFinal(encryptedData + initialVector)

    private fun CipherData.verifyHmacValue(mac: Mac) {
        val expectedHmacValue = mac.calculateHmacValue(
            encryptedData = encryptedData,
            initialVector = initialVector
        )

        if (!MessageDigest.isEqual(expectedHmacValue, hmacValue)) {
            throw SecurityException("Cipher text has been tampered with.")
        }
    }

    private enum class EncryptionMode {
        UNINITIALIZED,
        KEYSTORE,
        SOFTWARE
    }

    companion object {
        private const val AES_KEY_ALIAS =
            "com.linecorp.android.security.encryption.StringAesCipher"

        private const val INTEGRITY_KEY_ALIAS =
            "com.linecorp.android.security.encryption.StringAesCipher.INTEGRITY_KEY"

        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_SIZE_IN_BIT = 256

        private const val TRANSFORMATION_FORMAT =
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"

        private const val SHARED_PREFS_ENCRYPTION = "com.linecorp.linesdk.encryption"
        private const val KEY_ENCRYPTION_MODE = "encryption_mode"
        private const val KEY_SALT = "salt"

        private const val MODE_KEYSTORE = "keystore"
        private const val MODE_SOFTWARE = "software"

        private const val SALT_SIZE_BYTES = 16
        private const val PBKDF2_ITERATIONS = 10000
        private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val AES_KEY_SIZE_BITS = 256
        private const val HMAC_KEY_SIZE_BITS = 256
    }
}
