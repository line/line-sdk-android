package com.linecorp.linesdk.openchat.ui

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
        withContext(Dispatchers.IO) {
            lineApiClient.createOpenChatRoom(openChatParameters)
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
        private val DEFAULT_CATEGORY = OpenChatCategory.Game
    }
}
