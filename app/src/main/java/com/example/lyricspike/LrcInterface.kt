package com.example.lyricspike

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface LrcInterface {

    @GET
    suspend fun getSong(@Url fileName: String): ResponseBody

}