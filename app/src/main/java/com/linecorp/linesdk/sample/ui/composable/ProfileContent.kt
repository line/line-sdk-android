package com.linecorp.linesdk.sample.ui.composable

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.linecorp.linesdk.LineProfile
import com.linecorp.linesdk.sample.R
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Composable
fun ProfileContent(
    userProfile: LineProfile? = null,
    nonLoginHintText: String = stringResource(R.string.not_login)
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
        ) {
            var shouldShowPlaceHolder by rememberSaveable { mutableStateOf(true) }

            if (shouldShowPlaceHolder || userProfile == null) {
                Image(
                    alignment = Alignment.Center,
                    painter = painterResource(R.drawable.default_avatar),
                    contentDescription = nonLoginHintText
                )
            }

            userProfile?.let {
                SubcomposeAsyncImage(
                    model = userProfile.pictureUrl,
                    contentDescription = userProfile.displayName,
                    alignment = Alignment.Center,
                    onSuccess = {
                        shouldShowPlaceHolder = false
                    },
                    loading = {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(CircleShape)
                )
            }
        }

        Text(
            userProfile?.displayName ?: nonLoginHintText,
            fontWeight = if (userProfile != null) FontWeight.Black else null,
            color = if (userProfile != null) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
    }
}

@Preview("ProfileContentPreview Not Login")
@Preview("ProfileContentPreview Not Login (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileContentNotLoginPreview() {
    LineSdkAndroidTheme {
        ProfileContent()
    }
}

@Preview("ProfileContentPreview Logged in")
@Preview("ProfileContentPreview Logged in (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileContentLoggedInPreview() {
    val mockProfile = runBlocking {
        withContext(Dispatchers.IO) {
            LineProfile(
                "123456789",
                "This is a Preview",
                Uri.parse(
                    "https://source.android.com/static/docs/setup/images/Android_symbol_green_RGB.png"
                ),
                "Hello World!"
            )
        }
    }

    LineSdkAndroidTheme {
        ProfileContent(mockProfile)
    }
}
