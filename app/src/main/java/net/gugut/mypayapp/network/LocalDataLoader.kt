package net.gugut.mypayapp.network

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.gugut.mypayapp.model.ApiModuleItem

object LocalDataLoader {
    fun loadShirtsData(context: Context): List<ApiModuleItem> {
        return try {
            val jsonString = context.assets.open("shirts.json")
                .bufferedReader()
                .use { it.readText() }

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val type = Types.newParameterizedType(
                Map::class.java,
                String::class.java,
                Types.newParameterizedType(List::class.java, ApiModuleItem::class.java)
            )

            val adapter = moshi.adapter<Map<String, List<ApiModuleItem>>>(type)
            val result = adapter.fromJson(jsonString)
            result?.get("teams") ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}