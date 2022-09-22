package com.linecorp.linesdk.sample.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.linecorp.linesdk.api.LineApiClient
import com.linecorp.linesdk.api.LineApiClientBuilder

abstract class LineApiViewModelBase(context: Context, channelId: String) : ViewModel() {
    protected val lineApiClient: LineApiClient = LineApiClientBuilder(context, channelId).build()
}
