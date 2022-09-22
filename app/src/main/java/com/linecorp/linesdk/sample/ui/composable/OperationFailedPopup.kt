package com.linecorp.linesdk.sample.ui.composable

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.linecorp.linesdk.sample.R
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme

@Composable
fun OperationFailedPopup(
    content: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        },
        modifier = modifier
    )
}

@Preview("OperationFailedPopupPreview")
@Preview("OperationFailedPopupPreview (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OperationFailedPopupPreview() {
    val failedMsg = stringResource(R.string.error_message_title)

    LineSdkAndroidTheme {
        OperationFailedPopup(failedMsg)
    }
}
