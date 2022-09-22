package com.linecorp.linesdk.sample.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme

@Composable
fun CodeBlockParagraph(
    text: String,
    textStyle: TextStyle = LocalTextStyle.current,
    paragraphStyle: ParagraphStyle
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        val verticalScrollState = rememberScrollState()

        Text(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(verticalScrollState),
            text = text,
            style = textStyle
                .merge(
                    MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )
                .merge(paragraphStyle)
        )
    }
}

@Preview("CodeBlockParagraph")
@Preview("CodeBlockParagraph (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CodeBlockParagraphPreview() {
    LineSdkAndroidTheme {
        CodeBlockParagraph(text = "print('Hello World!')", paragraphStyle = ParagraphStyle())
    }
}
