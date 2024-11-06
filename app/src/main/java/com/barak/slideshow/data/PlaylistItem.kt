package com.barak.slideshow.data

import com.google.gson.annotations.SerializedName

data class PlaylistItem(
    @SerializedName("creativeKey") val creativeKey: String,
    @SerializedName("duration") val duration: Int
)