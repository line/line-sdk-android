package com.linecorp.linesdk.openchat

import android.text.Editable
import android.text.TextWatcher


/**
 * This class is TextWatcher to get current text and its length string relative to max length.
 */
class TextUpdateWatcher(
    private val updateStringsAction: (String, String) -> Unit,
    private val maxCount: Int
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        val text = s?.toString().orEmpty()
        updateStringsAction(text, "${text.length}/$maxCount")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
}
