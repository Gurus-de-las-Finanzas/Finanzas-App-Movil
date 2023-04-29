package com.example.finanzas.security.models

import com.google.gson.annotations.SerializedName

class AuthenticateResponse (
        @SerializedName("id")
        var id: Int,
        @SerializedName("name")
        var name: String,
        @SerializedName("lastName")
        var lastName: String,
        @SerializedName("age")
        var age: Int,
        @SerializedName("image")
        var image: String?,
        @SerializedName("email")
        var email: String,
        @SerializedName("token")
        var token: String
        )