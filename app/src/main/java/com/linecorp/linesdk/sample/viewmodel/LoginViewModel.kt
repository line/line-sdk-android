package com.linecorp.linesdk.sample.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.linecorp.linesdk.LineProfile
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineAuthenticationParams.WebAuthenticationMethod
import com.linecorp.linesdk.auth.LineAuthenticationParams.WebAuthenticationMethod.email
import com.linecorp.linesdk.auth.LineAuthenticationParams.WebAuthenticationMethod.qrCode
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.auth.LineLoginResult
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(context: Context, channelId: String) :
    LineApiViewModelBase(context, channelId) {

    private val _operationFailedPopupMsgFlow = MutableStateFlow<String?>(null)
    val operationFailedPopupMsgFlow
        get(): StateFlow<String?> = _operationFailedPopupMsgFlow

    private val _isLoginFlow = MutableStateFlow(false)
    val isLoginFlow
        get(): StateFlow<Boolean> = _isLoginFlow

    private val _userProfileFlow = MutableStateFlow<LineProfile?>(null)
    val userProfileFlow
        get(): StateFlow<LineProfile?> = _userProfileFlow

    init {
        updateLoginStatus()
    }

    fun createLoginIntent(
        context: Context,
        channelId: String,
        scopes: List<Scope> = emptyList(),
        nonce: String? = null,
        botPrompt: LineAuthenticationParams.BotPrompt? = null,
        uiLocale: Locale? = null,
        forceWebLogin: Boolean = false,
        qrCodeLogin: Boolean = false
    ): Intent {
        val loginAuthParam = createLoginAuthParam(
            scopes,
            nonce,
            botPrompt,
            uiLocale,
            webAuthMethod = if (qrCodeLogin) qrCode else email
        )

        return if (forceWebLogin) {
            LineLoginApi.getLoginIntentWithoutLineAppAuth(
                context,
                channelId,
                loginAuthParam
            )
        } else {
            LineLoginApi.getLoginIntent(
                context,
                channelId,
                loginAuthParam
            )
        }
    }

    fun processLoginResult(result: LineLoginResult) = with(result) {
        if (!isSuccess) {
            processFailureMsg(responseCode.name, errorData.message)
            return
        }

        if (lineProfile == null) {
            processFailureMsg("lineProfile of LineLoginResult is null.", errorData.message)
            return
        }

        _isLoginFlow.update { true }
        _userProfileFlow.update { lineProfile }
    }

    fun processLoginIntent(resultCode: Int, intent: Intent?) {
        if (!isResultCodeOk(resultCode)) {
            val errorMessage = intent?.dataString ?: "login error but no error message"
            processFailureMsg(errorMessage)
            return
        }

        if (intent == null) {
            processFailureMsg("success but no intent")
            return
        }

        val loginResult = LineLoginApi.getLoginResultFromIntent(intent)
        processLoginResult(loginResult)
    }

    fun logout() {
        viewModelScope.launch {
            val isLogoutSuccess = withContext(Dispatchers.IO) {
                lineApiClient.logout().isSuccess
            }

            if (isLogoutSuccess) {
                _isLoginFlow.update { false }
                _userProfileFlow.update { null }
            } else {
                showFailedPopup(LOGOUT_NOT_FINISHED_MSG)
            }
        }
    }

    fun dismissFailedPopup() {
        _operationFailedPopupMsgFlow.value = null
    }

    private fun createLoginAuthParam(
        scopes: List<Scope>,
        nonce: String?,
        botPrompt: LineAuthenticationParams.BotPrompt?,
        uiLocale: Locale?,
        webAuthMethod: WebAuthenticationMethod
    ): LineAuthenticationParams = LineAuthenticationParams.Builder()
        .scopes(scopes)
        .nonce(nonce)
        .uiLocale(uiLocale)
        .botPrompt(botPrompt)
        .initialWebAuthenticationMethod(webAuthMethod)
        .build()

    private fun fetchAndUpdateUserProfile() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _userProfileFlow.update { lineApiClient.profile.responseData }
            }
        }
    }

    private fun updateLoginStatus() {
        viewModelScope.launch {
            val isLogin = withContext(Dispatchers.IO) {
                lineApiClient.currentAccessToken.isSuccess
            }

            _isLoginFlow.update { isLogin }

            if (isLogin) {
                fetchAndUpdateUserProfile()
            }
        }
    }

    private fun showFailedPopup(errorMessage: String) {
        _operationFailedPopupMsgFlow.value = errorMessage
    }

    private fun isResultCodeOk(resultCode: Int): Boolean = resultCode == Activity.RESULT_OK

    private fun processFailureMsg(msg: String, vararg additionalMsgs: String?) {
        Log.e(TAG, msg)
        additionalMsgs.forEach {
            if (it != null) {
                Log.d(TAG, it)
            }
        }
        showFailedPopup(msg)
    }

    companion object {
        private const val TAG = "LoginViewModel"
        private const val LOGOUT_NOT_FINISHED_MSG = "Logout executed but may not finish."

        fun createFactory(context: Context, channelId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    when {
                        modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                            LoginViewModel(context, channelId) as T

                        else -> throw IllegalArgumentException("Not supported.")
                    }
            }
    }
}
