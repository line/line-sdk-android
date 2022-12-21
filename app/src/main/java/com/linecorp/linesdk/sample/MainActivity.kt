package com.linecorp.linesdk.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linecorp.linesdk.LoginDelegate
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.sample.ui.composable.AppBar
import com.linecorp.linesdk.sample.ui.composable.OperationFailedPopup
import com.linecorp.linesdk.sample.ui.composable.ProfileContent
import com.linecorp.linesdk.sample.ui.homeScreen.LoginButtonGroup
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme
import com.linecorp.linesdk.sample.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.createFactory(
            this,
            getString(R.string.default_channel_id)
        )
    }

    private val loginResultLauncher =
        registerForActivityResult(StartActivityForResult()) { activityResult ->
            loginViewModel.processLoginIntent(
                activityResult.resultCode,
                activityResult.data
            )
        }

    // A delegate for delegating the login result to the internal login handler of the LineLoginButton.
    private val loginDelegate = LoginDelegate.Factory.create()

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channelId = getString(R.string.default_channel_id)
        val appName = getString(R.string.app_name)
        val scopeList = listOf(Scope.PROFILE, Scope.OPENID_CONNECT)
        val failedToLoginOrLogoutMsgTitle = getString(R.string.error_message_title)

        setContent {
            LineSdkAndroidTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Scaffold(
                        topBar = {
                            AppBar(title = appName)
                        }
                    ) { innerPadding ->

                        val innerTopPadding = remember {
                            innerPadding.calculateTopPadding()
                        }

                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = innerTopPadding
                                )
                        ) {
                            val userProfile by loginViewModel.userProfileFlow.collectAsState()

                            ProfileContent(userProfile)

                            val operationFailedPopupMsg by loginViewModel.operationFailedPopupMsgFlow.collectAsState()
                            if (operationFailedPopupMsg != null) {
                                OperationFailedPopup(
                                    "${failedToLoginOrLogoutMsgTitle}\n\n" +
                                        "$operationFailedPopupMsg"
                                ) {
                                    loginViewModel.dismissFailedPopup()
                                }
                            }

                            LoginButtonGroup(
                                loginViewModel = loginViewModel,
                                channelId = channelId,
                                scopeList = scopeList,
                                loginDelegateForLineLoginBtn = loginDelegate,
                                onSimpleLoginButtonPressed = loginResultLauncher::launch
                            )
                        }
                    }
                }
            }
        }
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.activity.ComponentActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // In order to receive and process the Activity Result to the `LineLoginButton`.
        loginDelegate.onActivityResult(requestCode, resultCode, data)
    }
}
