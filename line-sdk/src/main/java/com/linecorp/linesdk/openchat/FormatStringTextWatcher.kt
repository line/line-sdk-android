package com.linecorp.linesdk.openchat

import android.text.Editable
import android.text.TextWatcher


/**
 * A text watcher formatting changed string to formatted string on text changed. The
 * [onTextChangedAction] is performed with parameters of changed text and formatted string on text
 * changed. For example, if the changed text is TEXT (string length is 4), then the formatted string
 * is 4/[maxCount].
 */
class FormatStringTextWatcher(
    private val onTextChangedAction: (changedString: String, FormattedString: String) -> Unit,
    private val maxCount: Int
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        val text = s?.toString().orEmpty()
        onTextChangedAction(text, "${text.length}/$maxCount")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
}
