package com.linecorp.linesdk.openchat.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linecorp.linesdk.LineApiResponse
import com.linecorp.linesdk.api.LineApiClient
import com.linecorp.linesdk.openchat.OpenChatCategory
import com.linecorp.linesdk.openchat.OpenChatParameters
import com.linecorp.linesdk.openchat.OpenChatRoomInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OpenChatInfoViewModel(
    private val sharedPreferences: SharedPreferences,
    private val lineApiClient: LineApiClient
) : ViewModel() {
    val chatroomName: MutableLiveData<String> = MutableLiveData()
    val profileName: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()
    val category: MutableLiveData<OpenChatCategory> = MutableLiveData()
    val isSearchIncluded: MutableLiveData<Boolean> = MutableLiveData()

    val openChatRoomInfo: LiveData<OpenChatRoomInfo> get() = _openChatRoomInfo
    private val _openChatRoomInfo: MutableLiveData<OpenChatRoomInfo> = MutableLiveData()

    val createChatRoomError: LiveData<LineApiResponse<OpenChatRoomInfo>> get() = _createChatRoomError
    private val _createChatRoomError: MutableLiveData<LineApiResponse<OpenChatRoomInfo>> =
        MutableLiveData()

    val isCreatingChatRoom: LiveData<Boolean> get() = _isCreatingChatRoom
    private val _isCreatingChatRoom: MutableLiveData<Boolean> = MutableLiveData()

    val shouldShowAgreementWarning: LiveData<Boolean> get() = _shouldShowAgreementWarning
    private val _shouldShowAgreementWarning: MutableLiveData<Boolean> = MutableLiveData()

    val isValid: LiveData<Boolean> = Transformations.map(chatroomName, String::isNotEmpty)
    val isProfileValid: LiveData<Boolean> = Transformations.map(profileName, String::isNotEmpty)

    init {
        chatroomName.value = ""
        profileName.value = getSavedProfileName()
        description.value = ""
        category.value = DEFAULT_CATEGORY
        isSearchIncluded.value = true

        checkAgreementStatus()
    }

    fun getCategoryStringArray(context: Context): Array<String> =
        OpenChatCategory.values()
            .map{ category -> context.resources.getString(category.resourceId)}
            .toTypedArray()

    fun getSelectedCategory(position: Int): OpenChatCategory =
        OpenChatCategory.values().getOrElse(position) { DEFAULT_CATEGORY }

    private fun checkAgreementStatus() {
        viewModelScope.launch {
            val result = checkAgreementStatusAsync()
            _shouldShowAgreementWarning.value = !result.isSuccess || !result.responseData
        }
    }

    fun createChatroom() {
        saveProfileName()

        val openChatParameters = generateOpenChatParameters()
        viewModelScope.launch {
            _isCreatingChatRoom.value = true

            val result = createChatRoomAsync(openChatParameters)
            if (result.isSuccess) {
                _openChatRoomInfo.value = result.responseData
            } else {
                _createChatRoomError.value = result
            }

            _isCreatingChatRoom.value = false
        }
    }

    private suspend fun createChatRoomAsync(openChatParameters: OpenChatParameters): LineApiResponse<OpenChatRoomInfo> =
        withContext(Dispatchers.IO) { lineApiClient.createOpenChatRoom(openChatParameters) }

    private suspend fun checkAgreementStatusAsync(): LineApiResponse<Boolean> =
        withContext(Dispatchers.IO) { lineApiClient.openChatAgreementStatus }

    private fun generateOpenChatParameters(): OpenChatParameters =
        OpenChatParameters(
            chatroomName.value.orEmpty(),
            description.value.orEmpty(),
            profileName.value.orEmpty(),
            category.value ?: DEFAULT_CATEGORY,
            isSearchIncluded.value ?: true
        )

    private fun saveProfileName() =
        sharedPreferences.edit { putString(KEY_PROFILE_NAME, profileName.value) }

    private fun getSavedProfileName(): String =
        sharedPreferences.getString(KEY_PROFILE_NAME, null).orEmpty()

    companion object {
        private val DEFAULT_CATEGORY = OpenChatCategory.NotSelected
        private const val KEY_PROFILE_NAME: String = "key_profile_name"
    }
}
