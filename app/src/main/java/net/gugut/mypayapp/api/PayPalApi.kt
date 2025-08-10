package net.gugut.mypayapp.api

import net.gugut.mypayapp.screen.OrderRequest
import retrofit2.Response
import retrofit2.http.*

interface PayPalApi {
    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun getAccessToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Response<AccessTokenResponse>

    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("PayPal-Request-Id") requestId: String,
        @Body orderRequest: OrderRequest
    ): Response<OrderResponse>
}

data class AccessTokenResponse(val access_token: String, val expires_in: Int)

data class OrderResponse(val id: String)
