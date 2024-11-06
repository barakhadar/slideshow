package com.barak.slideshow.data

import com.google.gson.annotations.SerializedName

data class PlaylistResponse(
    @SerializedName("screenKey") val screenKey: String,
    @SerializedName("playlists") val playlists: List<Playlist>
)