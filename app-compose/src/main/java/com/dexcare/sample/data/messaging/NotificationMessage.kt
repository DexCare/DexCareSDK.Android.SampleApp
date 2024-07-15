package com.dexcare.sample.data.messaging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationMessage(
    val title: String,
    val message: String,
    val url: String
) : Parcelable
