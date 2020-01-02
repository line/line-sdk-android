package com.linecorp.linesdk.openchat.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.linecorp.linesdk.openchat.OpenChatCategory

class OpenChatInfoViewModel : ViewModel() {
    val name: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()
    val category: MutableLiveData<OpenChatCategory> = MutableLiveData()
    val isSearchIncluded: MutableLiveData<Boolean> = MutableLiveData()

    val isValid: MutableLiveData<Boolean> = MutableLiveData()

    init {
        name.value = ""
        description.value = ""
        category.value = DEFAULT_CATEGORY
        isSearchIncluded.value = true
        isValid.value = false
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
        val nameLength = name.value?.length ?: 0

        isValid.value = nameLength in 1 until MAX_CHAT_NAME_LENGTH
    }

    fun setName(name: String) {
        this.name.value = name
        updateValidity()
    }

    fun setDescription(description: String) {
        this.description.value = description
        updateValidity()
    }

    companion object {
        const val MAX_CHAT_NAME_LENGTH = 50
        const val MAX_CHAT_DESCRIPTION_LENGTH = 200
        private val DEFAULT_CATEGORY = OpenChatCategory.Game
    }
}
