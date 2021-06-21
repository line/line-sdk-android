package com.linecorp.linesdk.api

/**
 * @hide
 * */
abstract class LineEnvConfig {
    open val apiServerBaseUri = "https://api.line.me/"
    open val openIdDiscoveryDocumentUrl = "https://access.line.me/.well-known/openid-configuration"
    open val webLoginPageUrl = "https://access.line.me/oauth2/v2.1/login"
}
