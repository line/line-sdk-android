package com.linecorp.android.security.encryption

data class CipherData(
    val encryptedString: String,
    val initialVector: String
) {
    override fun toString(): String = "$encryptedString$SEPARATOR$initialVector"

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
                    CipherData(get(INDEX_ENCRYPTED_STRING), get(INDEX_IV))
                }
                ?: throw IllegalArgumentException("Failed to split encrypted text `$encryptedData`")
    }
}