package com.linecorp.android.security.encryption

import android.util.Base64

class CipherData(
    val encryptedData: ByteArray,
    val initialVector: ByteArray,
    val hmacValue: ByteArray,
) {
    fun encodeToBase64String(): String =
        "${encryptedData.encodeBase64()}$SEPARATOR" +
            "${initialVector.encodeBase64()}$SEPARATOR" +
            hmacValue.encodeBase64()

    companion object {
        private const val SEPARATOR = ";"
        private const val INDEX_ENCRYPTED_DATA = 0
        private const val INDEX_IV = 1
        private const val INDEX_HMAC = 2
        private const val SIZE = 3

        fun decodeFromBase64String(cipherDataString: String): CipherData =
            cipherDataString
                .split(SEPARATOR)
                .takeIf { it.size == SIZE }
                ?.run {
                    CipherData(
                        encryptedData = get(INDEX_ENCRYPTED_DATA).decodeBase64(),
                        initialVector = get(INDEX_IV).decodeBase64(),
                        hmacValue = get(INDEX_HMAC).decodeBase64()
                    )
                }
                ?: throw IllegalArgumentException("Failed to split encrypted text `$cipherDataString`")
    }
}

private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

private fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)
