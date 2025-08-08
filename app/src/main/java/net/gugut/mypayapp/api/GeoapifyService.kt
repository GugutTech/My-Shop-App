package net.gugut.mypayapp.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyService {
    @GET("v1/geocode/search")
    suspend fun getCityStateFromZip(
        @Query("text") zip: String,
        @Query("apiKey") apiKey: String
    ): GeoapifyResponse
}
