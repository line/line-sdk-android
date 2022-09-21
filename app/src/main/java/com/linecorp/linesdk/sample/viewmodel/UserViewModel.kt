package com.linecorp.linesdk.sample.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.linecorp.linesdk.LineApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(context: Context, channelId: String) :
    LineApiViewModelBase(context, channelId) {

    private val _apiResponseString = MutableStateFlow("")
    val apiResponseString
        get(): StateFlow<String> = _apiResponseString

    fun getCurrentAccessToken() {
        runApi {
            val getTokenResponse = lineApiClient.currentAccessToken
            showResponseFrom(getTokenResponse)
        }
    }

    fun refreshAccessToken() {
        runApi {
            val refreshTokenResponse = lineApiClient.refreshAccessToken()
            showResponseFrom(refreshTokenResponse)
        }
    }

    fun verifyToken() {
        runApi {
            val verifyTokenResponse = lineApiClient.verifyToken()
            showResponseFrom(verifyTokenResponse)
        }
    }

    fun getUserProfile() {
        runApi {
            val userProfileResponse = lineApiClient.profile
            showResponseFrom(userProfileResponse)
        }
    }

    fun getFriendshipStatus() {
        runApi {
            val friendshipStatusResponse = lineApiClient.friendshipStatus
            showResponseFrom(friendshipStatusResponse)
        }
    }

    private fun <T> runApi(block: suspend CoroutineScope.() -> T) {
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) { block() }
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.toString())
            _apiResponseString.update { exception.toString() }
        }
    }

    private fun showResponseFrom(response: LineApiResponse<*>) {
        val logData = if (response.isSuccess) {
            response.responseData
        } else {
            response.errorData
        }

        _apiResponseString.update { logData.toString() }
    }

    companion object {
        private const val TAG = "UserViewModel"
        fun createFactory(context: Context, channelId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    when {
                        modelClass.isAssignableFrom(UserViewModel::class.java) ->
                            UserViewModel(context, channelId) as T
                        else -> throw IllegalArgumentException("Not supported.")
                    }
            }
    }
}
