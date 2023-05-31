package com.example.finanzas.shared

import com.example.finanzas.security.models.User

object StateManager {
    lateinit var authToken: String
    var loggedUserId: Int = -1
    lateinit var loggedUser: User
    var password: String = ""
    var frenchButtonActivated: Boolean = false
    var addClientButtonActivated: Boolean = false
}