package com.linecorp.android.security.encryption

import android.util.Base64

class CipherData(
    val encryptedByteArray: ByteArray,
    val initialVector: ByteArray
) {
    override fun toString(): String = "${encryptedByteArray.encodeBase64()}$SEPARATOR$initialVector"

    companion object {
        private const val SEPARATOR = ";"
        private const val INDEX_ENCRYPTED_STRING = 0
        private const val INDEX_IV = 1
        private const val SIZE = 2

        @Throws(IllegalArgumentException::class)
        fun from(encryptedData: String): CipherData =
            encryptedData
                .split(SEPARATOR)
                .takeIf { it.size == SIZE }
                ?.run {
                    CipherData(get(INDEX_ENCRYPTED_STRING).decodeBase64(), get(INDEX_IV).decodeBase64())
                }
                ?: throw IllegalArgumentException("Failed to split encrypted text `$encryptedData`")
    }
}

private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

private fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)
