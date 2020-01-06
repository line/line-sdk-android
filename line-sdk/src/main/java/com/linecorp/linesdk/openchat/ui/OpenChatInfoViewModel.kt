package com.linecorp.linesdk.openchat.ui

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

    val isValid: MutableLiveData<Boolean> = MutableLiveData()
    val isProfileValid: MutableLiveData<Boolean> = MutableLiveData()

    init {
        chatroomName.value = ""
        profileName.value = ""
        description.value = ""
        category.value = DEFAULT_CATEGORY
        isSearchIncluded.value = true
        isValid.value = false
        isProfileValid.value = false
    }

    fun getCategoryString(): String {
        // TODO: use translation strings corresponding to system locale
        return category.value?.defaultString.orEmpty()
    }

    fun getCategoryStringArray(): Array<String> =
        OpenChatCategory.values().map { it.defaultString }.toTypedArray()

    fun getSelectedCategory(position: Int): OpenChatCategory {
        if (position > OpenChatCategory.values().size) return DEFAULT_CATEGORY
        return OpenChatCategory.values()[position]
    }

    private fun updateValidity() {
        val nameLength = chatroomName.value?.length ?: 0
        val profileNameLength = profileName.value?.length ?: 0

        isValid.value = nameLength > 0
        isProfileValid.value = profileNameLength > 0
    }

    fun setChatroomName(name: String) {
        chatroomName.value = name
        updateValidity()
    }

    fun setDescription(description: String) {
        this.description.value = description
        updateValidity()
    }

    fun setProfileName(name: String) {
        profileName.value = name
        updateValidity()
    }

    fun toOpenChatParameters(): OpenChatParameters =
        OpenChatParameters(
            chatroomName.value.orEmpty(),
            description.value.orEmpty(),
            profileName.value.orEmpty(),
            category.value ?: DEFAULT_CATEGORY,
            isSearchIncluded.value ?: true
        )

    companion object {
        const val MAX_CHAT_NAME_LENGTH = 50
        const val MAX_CHAT_DESCRIPTION_LENGTH = 200
        private val DEFAULT_CATEGORY = OpenChatCategory.Game
    }
}
