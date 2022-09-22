package com.linecorp.linesdk.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.unit.dp
import com.linecorp.linesdk.sample.ui.apiListScreen.ApiButtonGroup
import com.linecorp.linesdk.sample.ui.composable.AppBar
import com.linecorp.linesdk.sample.ui.composable.CodeBlockParagraph
import com.linecorp.linesdk.sample.ui.theme.LineSdkAndroidTheme
import com.linecorp.linesdk.sample.viewmodel.UserViewModel

@ExperimentalMaterial3Api
class ApiListActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.createFactory(
            this,
            getString(R.string.default_channel_id)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = getString(R.string.api_list_activity_title)

        setContent {
            LineSdkAndroidTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Scaffold(
                        topBar = {
                            AppBar(title = title)
                        }
                    ) { innerPadding ->

                        val innerTopPadding = remember {
                            innerPadding.calculateTopPadding()
                        }

                        val log by userViewModel.apiResponseString.collectAsState()

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .widthIn(0.dp, 360.dp)
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = innerTopPadding
                                    )
                            ) {
                                SelectionContainer(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .height(240.dp)
                                ) {
                                    CodeBlockParagraph(
                                        text = log,
                                        paragraphStyle = ParagraphStyle()
                                    )
                                }

                                Surface(
                                    shape = MaterialTheme.shapes.extraLarge,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                ) {
                                    Divider(
                                        thickness = 3.dp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                ApiButtonGroup(
                                    userViewModel = userViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ApiListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
