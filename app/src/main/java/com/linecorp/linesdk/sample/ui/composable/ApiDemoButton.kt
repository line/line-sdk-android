package com.linecorp.linesdk.sample.ui.composable

import android.content.res.Configuration
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme

typealias OnClickAction = () -> Unit

@Composable
fun ApiDemoButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    emphasize: Boolean = false,
    onClickAction: OnClickAction = {}
) {
    ElevatedButton(
        onClickAction,
        modifier,
        enabled,
        colors = if (emphasize) {
            ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.elevatedButtonColors()
        }
    ) {
        Text(text)
    }
}

@Preview("ApiDemoButtonPreview")
@Preview("ApiDemoButtonPreview (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ApiDemoButtonPreview() {
    LineSdkAndroidTheme {
        ApiDemoButton(
            text = "preview"
        )
    }
}

@Preview("Emphasized ApiDemoButton")
@Preview("Emphasized ApiDemoButton (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ApiDemoButtonEmphasizedPreview() {
    LineSdkAndroidTheme {
        ApiDemoButton(
            text = "preview",
            emphasize = true
        )
    }
}

@Preview("Disabled ApiDemoButton", showBackground = true)
@Preview(
    "Disabled ApiDemoButton (dark)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun ApiDemoButtonDisabledPreview() {
    LineSdkAndroidTheme {
        ApiDemoButton(
            text = "preview",
            enabled = false
        )
    }
}
