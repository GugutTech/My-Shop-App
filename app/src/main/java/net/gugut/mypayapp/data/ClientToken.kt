package net.gugut.mypayapp.data

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// ClientToken.kt
data class ClientToken(val value: String? = null)

// Api.kt
interface Api {
    @GET("/client_token")
    fun getClientToken(): Call<ClientToken>
}

// Define ClientTokenProvider interface and callback
interface ClientTokenProvider {
    fun getClientToken(callback: ClientTokenCallback)
}

interface ClientTokenCallback {
    fun onSuccess(token: String)
    fun onFailure(exception: Exception)
}

// ExampleClientTokenProvider.kt
internal class ExampleClientTokenProvider : ClientTokenProvider {

    override fun getClientToken(callback: ClientTokenCallback) {
        val call: Call<ClientToken> = createService().getClientToken()
        call.enqueue(object : Callback<ClientToken> {
            override fun onResponse(call: Call<ClientToken>, response: Response<ClientToken>) {
                response.body()?.value?.let {
                    callback.onSuccess(it)
                } ?: callback.onFailure(Exception("Empty client token"))
            }

            override fun onFailure(call: Call<ClientToken>, t: Throwable) {
                callback.onFailure(Exception(t))
            }
        })
    }

    companion object {
        private val builder = Retrofit.Builder()
            .baseUrl("https://my-api.com/") // Replace with your backend URL
            .addConverterFactory(GsonConverterFactory.create())

        private val httpClient = OkHttpClient.Builder()

        fun createService(): Api {
            builder.client(httpClient.build())
            val retrofit = builder.build()
            return retrofit.create(Api::class.java)
        }
    }
}
