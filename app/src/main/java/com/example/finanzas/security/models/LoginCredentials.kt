package com.example.finanzas.security.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class LoginCredentials(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,

    @ColumnInfo
    var email: String,

    @ColumnInfo
    var password: String
)