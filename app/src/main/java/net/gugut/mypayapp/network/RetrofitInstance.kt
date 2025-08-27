// RetrofitInstance.kt
package net.gugut.mypayapp.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
//    private val moshi = Moshi.Builder().build()
//
//    val api: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl("http://192.168.254.183:3000/") // Android emulator → localhost
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//            .create(ApiService::class.java)
//    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.254.183:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
