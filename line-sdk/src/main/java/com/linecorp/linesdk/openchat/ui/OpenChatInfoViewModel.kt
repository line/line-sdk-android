package com.linecorp.linesdk.openchat.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linecorp.linesdk.LineApiResponse
import com.linecorp.linesdk.api.OpenChatApiClient
import com.linecorp.linesdk.openchat.OpenChatCategory
import com.linecorp.linesdk.openchat.OpenChatParameters
import com.linecorp.linesdk.openchat.OpenChatRoomInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OpenChatInfoViewModel(
    private val openChatApiClient: OpenChatApiClient
): ViewModel() {
    val chatroomName: MutableLiveData<String> = MutableLiveData()
    val profileName: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()
    val category: MutableLiveData<OpenChatCategory> = MutableLiveData()
    val isSearchIncluded: MutableLiveData<Boolean> = MutableLiveData()

    val openChatRoomInfo: MutableLiveData<OpenChatRoomInfo> = MutableLiveData()
    val createChatRoomError: MutableLiveData<LineApiResponse<OpenChatRoomInfo>> = MutableLiveData()
    val isCreatingChatRoom: MutableLiveData<Boolean> = MutableLiveData()

    val isValid: LiveData<Boolean> = Transformations.map(chatroomName, String::isNotEmpty)
    val isProfileValid: LiveData<Boolean> = Transformations.map(profileName, String::isNotEmpty)

    init {
        chatroomName.value = ""
        profileName.value = ""
        description.value = ""
        category.value = DEFAULT_CATEGORY
        isSearchIncluded.value = true
    }

    fun getCategoryString(): String {
        // TODO: use translation strings corresponding to system locale
        return category.value?.defaultString.orEmpty()
    }

    fun getCategoryStringArray(): Array<String> =
        OpenChatCategory.values().map(OpenChatCategory::defaultString).toTypedArray()

    fun getSelectedCategory(position: Int): OpenChatCategory =
        OpenChatCategory.values().getOrElse(position) { DEFAULT_CATEGORY }

    fun createChatroom() {
        val openChatParameters = generateOpenChatParameters()

        viewModelScope.launch {
            isCreatingChatRoom.value = true

            val result = createChatRoomAsync(openChatParameters)
            if (result.isSuccess) {
                openChatRoomInfo.value =  result.responseData
            } else {
                createChatRoomError.value = result
            }

            isCreatingChatRoom.value = false
        }
    }

    private suspend fun createChatRoomAsync(openChatParameters: OpenChatParameters) =
        withContext(Dispatchers.IO) {
            openChatApiClient.createOpenChatRoom(openChatParameters)
        }

    private fun generateOpenChatParameters(): OpenChatParameters =
        OpenChatParameters(
            chatroomName.value.orEmpty(),
            description.value.orEmpty(),
            profileName.value.orEmpty(),
            category.value ?: DEFAULT_CATEGORY,
            isSearchIncluded.value ?: true
        )

    companion object {
        const val MAX_PROFILE_NAME_LENGTH = 20
        const val MAX_CHAT_NAME_LENGTH = 50
        const val MAX_CHAT_DESCRIPTION_LENGTH = 200
        private val DEFAULT_CATEGORY = OpenChatCategory.Game
    }
}
