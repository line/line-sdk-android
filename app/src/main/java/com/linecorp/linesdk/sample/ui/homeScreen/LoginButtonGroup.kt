package com.linecorp.linesdk.sample.ui.homeScreen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    var forceWebLogin by rememberSaveable { mutableStateOf(false) }
    var qrCodeLogin by rememberSaveable { mutableStateOf(false) }

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
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                enabled = !isLogin
            ) {
                val intent = loginViewModel.createLoginIntent(
                    context,
                    channelId,
                    scopeList,
                    forceWebLogin = forceWebLogin,
                    qrCodeLogin = qrCodeLogin
                )
                onSimpleLoginButtonPressed(intent)
            }

            LabeledCheckbox(
                label = "Force web login",
                checked = forceWebLogin,
                onCheckedChange = { forceWebLogin = it }
            )

            LabeledCheckbox(
                label = "QR code login",
                checked = qrCodeLogin,
                onCheckedChange = { qrCodeLogin = it }
            )

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

@Composable
fun LabeledCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(text = label)
    }
}