package com.linecorp.linesdk.openchat.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ProfileInfoViewModel : ViewModel() {
    val displayName: MutableLiveData<String> = MutableLiveData()
    val isValid: MutableLiveData<Boolean> = MutableLiveData()

    init {
        displayName.value = ""
        isValid.value = false
    }

    private fun updateValidity() {
        val nameLength = displayName.value?.length ?: 0

        isValid.value = nameLength > 0
    }

    fun setDisplayName(name: String) {
        this.displayName.value = name
        updateValidity()
    }
}
