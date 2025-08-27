package net.gugut.mypayapp.network

import net.gugut.mypayapp.model.ApiModuleItem
import retrofit2.http.GET

interface ApiService {
    @GET("api/shirts") // your endpoint
    suspend fun getShirts(): List<ApiModuleItem>
}