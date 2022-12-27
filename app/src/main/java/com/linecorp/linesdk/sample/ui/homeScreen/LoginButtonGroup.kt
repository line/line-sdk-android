package com.linecorp.linesdk.sample.ui.homeScreen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.linecorp.linesdk.LoginDelegate
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.sample.ApiListActivity
import com.linecorp.linesdk.sample.ui.composable.ApiDemoButton
import com.linecorp.linesdk.sample.ui.composable.LineLoginButton
import com.linecorp.linesdk.sample.viewmodel.LoginViewModel

@ExperimentalMaterial3Api
@Composable
fun LoginButtonGroup(
    loginViewModel: LoginViewModel,
    channelId: String,
    scopeList: List<Scope>,
    loginDelegateForLineLoginBtn: LoginDelegate,
    onSimpleLoginButtonPressed: (Intent) -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .padding(
                start = 32.dp,
                end = 32.dp
            )
    ) {
        item {
            val context = LocalContext.current
            val isLogin by loginViewModel.isLoginFlow.collectAsState()

            LineLoginButton(
                channelId,
                modifier = Modifier
                    .fillMaxWidth(),
                loginDelegate = loginDelegateForLineLoginBtn,
                handleLoginResult = loginViewModel::processLoginResult
            )

            ApiDemoButton(
                "login",
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = !isLogin
            ) {
                val intent = loginViewModel.createLoginIntent(
                    context,
                    channelId,
                    scopeList
                )
                onSimpleLoginButtonPressed(intent)
            }

            ApiDemoButton(
                "web login",
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = !isLogin
            ) {
                val intent = loginViewModel.createLoginIntent(
                    context,
                    channelId,
                    scopeList,
                    onlyWebLogin = true
                )
                onSimpleLoginButtonPressed(intent)
            }

            ApiDemoButton(
                "logout",
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = isLogin
            ) {
                loginViewModel.logout()
            }

            ApiDemoButton(
                "Api List Page",
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = isLogin,
                emphasize = true
            ) {
                ApiListActivity.start(context)
            }
        }
    }
}
