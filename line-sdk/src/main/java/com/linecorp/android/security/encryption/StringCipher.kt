package com.linecorp.android.security.encryption

import android.content.Context

interface StringCipher {
    fun initialize(context: Context)

    fun encrypt(context: Context, plainText: String): String

    fun decrypt(context: Context, b64CipherText: String): String
}
