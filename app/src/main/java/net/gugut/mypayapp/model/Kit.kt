package net.gugut.mypayapp.model

import com.squareup.moshi.Json

data class Kit(
    @Json(name = "id")
    val id: String,
    @Json(name = "imageUrl")
    val imageUrl: String?,
    @Json(name = "comingSoon")
    val comingSoon: String?,
    @Json(name = "inStock")
    val inStock: Boolean,
    @Json(name = "kitType")
    val kitType: String,
    @Json(name = "price")
    val price: Double,
    @Json(name = "season")
    val season: String,
    @Json(name = "sizeOptions")
    val sizeOptions: List<String>
)