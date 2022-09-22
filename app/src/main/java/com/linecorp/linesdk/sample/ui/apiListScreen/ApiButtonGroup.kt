package com.linecorp.linesdk.sample.ui.apiListScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linecorp.linesdk.sample.ui.composable.ApiDemoButton
import com.linecorp.linesdk.sample.viewmodel.UserViewModel

@ExperimentalMaterial3Api
@Composable
fun ApiButtonGroup(
    userViewModel: UserViewModel
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .widthIn(0.dp, 320.dp)
                .padding(
                    start = 8.dp,
                    end = 8.dp
                )
        ) {
            item {
                ApiDemoButton(
                    "Get profile",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userViewModel.getUserProfile()
                }

                ApiDemoButton(
                    "Get Current AccessToken",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userViewModel.getCurrentAccessToken()
                }

                ApiDemoButton(
                    "Refresh AccessToken",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userViewModel.refreshAccessToken()
                }

                ApiDemoButton(
                    "verify AccessToken",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userViewModel.verifyToken()
                }

                ApiDemoButton(
                    "Get FriendshipStatus",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userViewModel.getFriendshipStatus()
                }
            }
        }
    }
}
