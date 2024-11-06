package com.barak.slideshow.data

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface NoviSignApi {
    @GET("PlayerBackend/screen/playlistItems/{key}")
    suspend fun getPlaylists(@Path("key") screenKey: String): PlaylistResponse

    @GET("PlayerBackend/creative/get/{fileKey}")
    suspend fun getMediaFile(@Path("fileKey") fileKey: String): ResponseBody
}