package com.linecorp.linesdk.openchat.ui

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linecorp.linesdk.openchat.OpenChatCategory
import com.linecorp.linesdk.openchat.OpenChatParameters

class OpenChatInfoViewModel : ViewModel() {
    val chatroomName: MutableLiveData<String> = MutableLiveData()
    val profileName: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()
    val category: MutableLiveData<OpenChatCategory> = MutableLiveData()
    val isSearchIncluded: MutableLiveData<Boolean> = MutableLiveData()

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

    fun toOpenChatParameters(): OpenChatParameters =
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
