package net.gugut.mypayapp.model

data class User(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String = "",
    val lastName: String = ""
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
