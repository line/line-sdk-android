package com.linecorp.linesdk.sample.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.linecorp.linesdk.LoginDelegate
import com.linecorp.linesdk.LoginListener
import com.linecorp.linesdk.auth.LineLoginResult
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme
import com.linecorp.linesdk.widget.LoginButton

@Composable
fun LineLoginButton(
    channelId: String,
    modifier: Modifier = Modifier,
    onLoginSuccess: (result: LineLoginResult) -> Unit = {},
    onLoginFailure: (result: LineLoginResult) -> Unit = {},
    loginDelegate: LoginDelegate,
) {
    val loginListener = object : LoginListener {
        override fun onLoginSuccess(result: LineLoginResult) =
            onLoginSuccess(result)

        override fun onLoginFailure(result: LineLoginResult?) =
            onLoginFailure(result ?: LineLoginResult.internalError("LineLoginResult is null"))
    }

    AndroidView({ LoginButton(it) }, modifier = modifier) { loginButton ->
        loginButton.apply {
            setChannelId(channelId)
            setLoginDelegate(loginDelegate)
            addLoginListener(loginListener)
        }
    }
}

@Preview("LineLoginButtonPreview")
@Composable
private fun LineLoginButtonPreview() {
    val dummyLoginDelegate = LoginDelegate.Factory.create()
    LineSdkAndroidTheme {
        LineLoginButton(
            channelId = "1234567",
            loginDelegate = dummyLoginDelegate
        )
    }
}
