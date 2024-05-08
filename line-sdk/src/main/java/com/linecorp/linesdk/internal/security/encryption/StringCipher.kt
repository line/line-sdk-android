package com.linecorp.linesdk.internal.security.encryption

import android.content.Context

interface StringCipher {

    fun initialize(context: Context)

    fun encrypt(context: Context, plainText: String): String

    fun decrypt(context: Context, cipherText: String): String
}
