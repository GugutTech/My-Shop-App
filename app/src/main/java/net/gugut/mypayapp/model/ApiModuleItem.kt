package net.gugut.mypayapp.model


import com.squareup.moshi.Json

data class ApiModuleItem(
    @Json(name = "kits")
    val kits: List<Kit>,
    @Json(name = "teamName")
    val teamName: String
)