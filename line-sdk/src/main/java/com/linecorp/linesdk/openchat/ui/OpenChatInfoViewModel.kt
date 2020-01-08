package com.linecorp.linesdk.openchat.ui

import android.os.AsyncTask
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linecorp.linesdk.LineApiResponse
import com.linecorp.linesdk.api.OpenChatApiClient
import com.linecorp.linesdk.openchat.OpenChatCategory
import com.linecorp.linesdk.openchat.OpenChatParameters
import com.linecorp.linesdk.openchat.OpenChatRoomInfo

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

    val isValid: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(chatroomName) { chatroomNameString ->
            value = chatroomNameString.isNotEmpty()
        }
    }

    val isProfileValid: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(profileName) { profileNameString ->
            value = profileNameString.isNotEmpty()
        }
    }

    private var createOpenChatroomTask: CreateOpenChatroomTask? = null

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
        createOpenChatroomTask?.cancel(true)

        createOpenChatroomTask =
            CreateOpenChatroomTask(
                openChatApiClient,
                openChatParameters,
                isCreatingChatRoom
            ) { result ->
                if (result.isSuccess) {
                    openChatRoomInfo.value =  result.responseData
                } else {
                    createChatRoomError.value = result
                }
            }
        createOpenChatroomTask?.execute()
    }


    override fun onCleared() {
        createOpenChatroomTask?.cancel(true)
        super.onCleared()
    }

    private fun generateOpenChatParameters(): OpenChatParameters =
        OpenChatParameters(
            chatroomName.value.orEmpty(),
            description.value.orEmpty(),
            profileName.value.orEmpty(),
            category.value ?: DEFAULT_CATEGORY,
            isSearchIncluded.value ?: true
        )

    private class CreateOpenChatroomTask(
        private val apiClient: OpenChatApiClient,
        private val parameters: OpenChatParameters,
        private val isCreatingChatRoom: MutableLiveData<Boolean>,
        private val postAction: (LineApiResponse<OpenChatRoomInfo>) -> Unit
    ) : AsyncTask<Void, Void, LineApiResponse<OpenChatRoomInfo>>() {
        override fun onPreExecute() {
            isCreatingChatRoom.value = true
        }

        override fun doInBackground(vararg params: Void): LineApiResponse<OpenChatRoomInfo> =
            apiClient.createOpenChatRoom(parameters)

        override fun onPostExecute(result: LineApiResponse<OpenChatRoomInfo>) {
            isCreatingChatRoom.value = false
            postAction(result)
        }
    }


    companion object {
        const val MAX_PROFILE_NAME_LENGTH = 20
        const val MAX_CHAT_NAME_LENGTH = 50
        const val MAX_CHAT_DESCRIPTION_LENGTH = 200
        private val DEFAULT_CATEGORY = OpenChatCategory.Game
    }
}
