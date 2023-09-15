package com.linecorp.android.security.encryption

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import android.security.keystore.KeyProperties.PURPOSE_VERIFY
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * AES cipher by AndroidKeyStore
 */
class StringAesCipher : StringCipher {
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEY_STORE).also {
            it.load(null)
        }
    }

    private lateinit var hmac: Mac

    override fun initialize(context: Context) {
        if (::hmac.isInitialized) {
            return
        }

        synchronized(this) {
            getAesSecretKey()
            val integrityKey = getIntegrityKey()

            hmac = Mac.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256).apply {
                init(integrityKey)
            }
        }
    }

    override fun encrypt(context: Context, plainText: String): String {
        synchronized(this) {
            initialize(context)

            try {
                val secretKey = getAesSecretKey()

                val cipher = Cipher.getInstance(TRANSFORMATION_FORMAT).apply {
                    init(Cipher.ENCRYPT_MODE, secretKey)
                }
                val encryptedData: ByteArray = cipher.doFinal(plainText.toByteArray())

                return CipherData(
                    encryptedData = encryptedData,
                    initialVector = cipher.iv,
                    hmacValue = hmac.calculateHmacValue(encryptedData, cipher.iv)
                ).encodeToBase64String()
            } catch (e: Exception) {
                throw EncryptionException("Failed to encrypt", e)
            }
        }
    }

    override fun decrypt(context: Context, cipherText: String): String {
        synchronized(this) {
            try {
                val secretKey = getAesSecretKey()

                val cipherData = CipherData.decodeFromBase64String(cipherText)

                cipherData.verifyHmacValue(hmac)

                val ivSpec = IvParameterSpec(cipherData.initialVector)

                return Cipher.getInstance(TRANSFORMATION_FORMAT)
                    .apply { init(Cipher.DECRYPT_MODE, secretKey, ivSpec) }
                    .run { doFinal(cipherData.encryptedData) }
                    .let {
                        String(it)
                    }
            } catch (e: Exception) {
                throw EncryptionException("Failed to decrypt", e)
            }
        }
    }

    private fun getAesSecretKey(): SecretKey {
        return if (keyStore.containsAlias(AES_KEY_ALIAS)) {
            val secretKeyEntry =
                keyStore.getEntry(AES_KEY_ALIAS, null) as KeyStore.SecretKeyEntry

            secretKeyEntry.secretKey
        } else {
            createAesKey()
        }
    }

    private fun getIntegrityKey(): SecretKey {
        return if (keyStore.containsAlias(INTEGRITY_KEY_ALIAS)) {
            val secretKeyEntry =
                keyStore.getEntry(INTEGRITY_KEY_ALIAS, null) as KeyStore.SecretKeyEntry

            secretKeyEntry.secretKey
        } else {
            createIntegrityKey()
        }
    }

    /**
     * Create AES key in AndroidKeyStore
     */
    private fun createAesKey(): SecretKey {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            AES_KEY_ALIAS,
            PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE_IN_BIT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build()

        keyGenerator.run {
            init(keyGenParameterSpec)
            return generateKey()
        }
    }

    private fun createIntegrityKey(): SecretKey {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEY_STORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            INTEGRITY_KEY_ALIAS,
            PURPOSE_SIGN or PURPOSE_VERIFY
        )
            .build()

        keyGenerator.run {
            init(keyGenParameterSpec)
            return generateKey()
        }
    }

    private fun Mac.calculateHmacValue(
        encryptedData: ByteArray,
        initialVector: ByteArray
    ): ByteArray = doFinal(encryptedData + initialVector)

    /**
     * Validate the HMAC value
     *
     * @throws SecurityException if the HMAC value doesn't match with [encryptedData]
     */
    private fun CipherData.verifyHmacValue(mac: Mac) {
        val expectedHmacValue: ByteArray = mac.calculateHmacValue(
            encryptedData = encryptedData,
            initialVector = initialVector
        )

        if (!MessageDigest.isEqual(expectedHmacValue, hmacValue)) {
            throw SecurityException("Cipher text has been tampered with.")
        }
    }

    companion object {
        private const val AES_KEY_ALIAS =
            "com.linecorp.android.security.encryption.StringAesCipher"

        private const val INTEGRITY_KEY_ALIAS =
            "com.linecorp.android.security.encryption.StringAesCipher.INTEGRITY_KEY"

        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_SIZE_IN_BIT = 256

        private const val TRANSFORMATION_FORMAT =
            KeyProperties.KEY_ALGORITHM_AES +
                "/${KeyProperties.BLOCK_MODE_CBC}" +
                "/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
    }
}
