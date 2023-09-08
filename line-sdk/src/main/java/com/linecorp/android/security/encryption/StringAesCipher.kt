package com.linecorp.android.security.encryption

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
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

    override fun initialize(context: Context) {
    }

    override fun encrypt(context: Context, plainText: String): String {
        val secretKey = try {
            getSecretKey()
        } catch (e: Exception) {
            throw EncryptionException("Failed to retrieve secret key!", e)
        }

        val cipher = Cipher.getInstance(TRANSFORMATION_FORMAT).apply {
            init(Cipher.ENCRYPT_MODE, secretKey)
        }
        val encryptedByteArray = try {
            cipher.doFinal(plainText.toByteArray())
        } catch (e: Exception) {
            throw EncryptionException("Failed to encrypt!", e)
        }

        return CipherData(encryptedByteArray, cipher.iv).toString()
    }

    override fun decrypt(context: Context, cipherText: String): String {
        val secretKey = try {
            getSecretKey()
        } catch (e: Exception) {
            throw EncryptionException("Failed to retrieve secret key!", e)
        }
        val cipherData = CipherData.from(cipherText)
        val ivSpec = IvParameterSpec(cipherData.initialVector)
        try {
            return Cipher.getInstance(TRANSFORMATION_FORMAT)
                .apply { init(Cipher.DECRYPT_MODE, secretKey, ivSpec) }
                .run { doFinal(cipherData.encryptedByteArray) }
                .let {
                    String(it)
                }
        } catch (e: Exception) {
            throw EncryptionException("Failed to decrypt!", e)
        }
    }

    private fun getSecretKey(): SecretKey? {
        if (!isAesKeyCreated()) {
            createAesKey()
        }
        val secretKeyEntry =
            keyStore.getEntry(AES_KEY_ALIAS, null) as KeyStore.SecretKeyEntry

        return secretKeyEntry.secretKey
    }

    private fun isAesKeyCreated(): Boolean = keyStore.containsAlias(AES_KEY_ALIAS)

    private fun createAesKey() {
        val keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM_NAME, ANDROID_KEY_STORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            AES_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(AES_KEY_SIZE_IN_BIT)
            .setBlockModes(AES_KEY_BLOCK_MODE)
            .setEncryptionPaddings(AES_KEY_PADDING)
            .build()

        keyGenerator.run {
            init(keyGenParameterSpec)
            generateKey()
        }
    }

    companion object {
        private const val AES_KEY_ALIAS =
            "com.linecorp.android.security.encryption.StringAesCipher"

        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val AES_ALGORITHM_NAME = KeyProperties.KEY_ALGORITHM_AES
        private const val AES_KEY_BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val AES_KEY_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7

        private const val AES_KEY_SIZE_IN_BIT = 256

        private const val TRANSFORMATION_FORMAT =
            "$AES_ALGORITHM_NAME/$AES_KEY_BLOCK_MODE/$AES_KEY_PADDING"
    }
}
