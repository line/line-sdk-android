package com.linecorp.android.security.encryption

import javax.crypto.SecretKey

class SecretKeys(
    val encryptionKey: SecretKey,
    val integrityKey: SecretKey
)
