package com.dt.auto.background.video.recorder.domain

import android.net.Uri
import androidx.annotation.Keep

@Keep
data class RecordingsDto(
    val id: Long,
    var datetime: String,
    var duration: String,
    var filename: String,
    var filepath: String,
    var size: String,
    val contentUri: Uri
    )