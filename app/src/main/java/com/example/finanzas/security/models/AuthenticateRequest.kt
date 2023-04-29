package com.example.finanzas.security.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthenticateRequest (
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("password")
    @Expose
    var password: String
)