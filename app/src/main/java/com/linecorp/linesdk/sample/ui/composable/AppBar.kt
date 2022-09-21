package com.linecorp.linesdk.sample.ui.composable

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme

@ExperimentalMaterial3Api
@Composable
fun AppBar(title: String, modifier: Modifier = Modifier, navController: NavController? = null) {
    val systemUiController = rememberSystemUiController()
    val appBarBackgroundColor = MaterialTheme.colorScheme.primary
    val appBarContentColor = MaterialTheme.colorScheme.onPrimary

    systemUiController.setStatusBarColor(appBarBackgroundColor)

    SmallTopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Black
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = appBarBackgroundColor,
            titleContentColor = appBarContentColor
        ),
        navigationIcon = {
            navController?.let { controller ->
                controller.previousBackStackEntry?.let {
                    IconButton({ controller.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview("AppBarPreview", showSystemUi = true, device = Devices.PIXEL)
@Preview(
    "AppBarPreview (dark)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    showBackground = true,
    device = Devices.PIXEL
)
@ExperimentalMaterial3Api
@Composable
private fun AppBarPreview() {
    LineSdkAndroidTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    AppBar(title = "preview")
                }
            ) { /** ignored */ }
        }
    }
}
