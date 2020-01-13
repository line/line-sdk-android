package com.linecorp.linesdk.openchat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OpenChatRoomInfo(
    val roomId: String,
    val landingPageUrl: String
) : Parcelable
