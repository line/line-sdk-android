package com.linecorp.android.security.encryption

import android.util.Base64

class CipherData(
    val encryptedData: ByteArray,
    val initialVector: ByteArray,
    val hmacValue: ByteArray,
) {
    fun encodeToBase64String(): String =
        listOf(encryptedData, initialVector, hmacValue)
            .joinToString(SEPARATOR) { it.encodeBase64() }

    companion object {
        private const val SEPARATOR = ";"
        private const val SIZE = 3

        fun decodeFromBase64String(cipherDataBase64String: String): CipherData {
            val parts = cipherDataBase64String.split(SEPARATOR)
            require(parts.size == SIZE) { "Failed to split encrypted text `$cipherDataBase64String`" }

            return CipherData(
                encryptedData = parts[0].decodeBase64(),
                initialVector = parts[1].decodeBase64(),
                hmacValue = parts[2].decodeBase64()
            )
        }
    }
}

private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

private fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)
