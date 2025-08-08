package net.gugut.mypayapp.api

data class GeoapifyResponse(
    val features: List<GeoapifyFeature>
)

data class GeoapifyFeature(
    val properties: GeoapifyProperties
)

data class GeoapifyProperties(
    val city: String?,
    val state: String?
)
