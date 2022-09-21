package com.linecorp.linesdk.sample.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.linecorp.linesdk.LineProfile
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.auth.LineLoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

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
        onlyWebLogin: Boolean = false
    ): Intent {
        val loginAuthParam = createLoginAuthParam(scopes, nonce, botPrompt, uiLocale)

        return if (onlyWebLogin) {
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

    fun processLoginResult(result: LineLoginResult) {
        if (result.isSuccess) {
            updateLoginStatusTo(true)
            _userProfileFlow.update { result.lineProfile }
        } else {
            Log.e(TAG, result.toString())
            val failReason = result.responseCode.name
            showFailedPopupWith(failReason)
        }
    }

    fun processLoginResultFromIntent(intent: Intent) {
        val loginResult = LineLoginApi.getLoginResultFromIntent(intent)
        processLoginResult(loginResult)
    }

    fun logout() {
        viewModelScope.launch {
            val isLogoutSuccess = withContext(Dispatchers.IO) {
                lineApiClient.logout().isSuccess
            }

            if (isLogoutSuccess) {
                updateLoginStatusTo(false)
                _userProfileFlow.update { null }
            } else {
                showFailedPopupWith(LOGOUT_NOT_FINISHED_MSG)
            }
        }
    }

    fun showFailedPopupWith(msg: String) {
        _operationFailedPopupMsgFlow.value = msg
    }

    fun dismissFailedPopup() {
        _operationFailedPopupMsgFlow.value = null
    }

    private fun createLoginAuthParam(
        scopes: List<Scope>,
        nonce: String?,
        botPrompt: LineAuthenticationParams.BotPrompt?,
        uiLocale: Locale?
    ): LineAuthenticationParams = LineAuthenticationParams.Builder()
        .scopes(scopes)
        .nonce(nonce)
        .uiLocale(uiLocale)
        .botPrompt(botPrompt)
        .build()

    private fun updateLoginStatusTo(isLogin: Boolean) {
        _isLoginFlow.update { isLogin }
    }

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

            updateLoginStatusTo(isLogin)

            if (isLogin) {
                fetchAndUpdateUserProfile()
            }
        }
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
