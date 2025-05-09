package com.linecorp.linesdk.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * ActivityResultContract of [LineLoginApi] that performs LINE Login.
 *
 * Usage
 * First, register for activity result contract
 * ```kotlin
 * val lineLogin = registerForActivityResult(LineLogin()) { result: LineLoginResult ->
 *     // Handle the returned result
 * }
 * ```
 * Then launch the login
 * ```
 * lineLogin.launch()
 * ```
 */
class LineLogin : ActivityResultContract<LineLogin.Input, LineLoginResult>() {

    data class Input(
        val channelId: String,
        val params: LineAuthenticationParams,
        val isBrowserLoginOnly: Boolean = false
    )

    override fun createIntent(context: Context, input: Input): Intent {
        val (channelId, params, isBrowserLoginOnly) = input
        return if (isBrowserLoginOnly) {
            LineLoginApi.getLoginIntentWithoutLineAppAuth(context, channelId, params)
        } else {
            LineLoginApi.getLoginIntent(context, channelId, params)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): LineLoginResult {
        return LineLoginApi.getLoginResultFromIntent(intent)
    }

}
