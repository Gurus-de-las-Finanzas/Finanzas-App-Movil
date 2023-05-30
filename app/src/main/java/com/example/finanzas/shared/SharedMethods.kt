package com.example.finanzas.shared

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.finanzas.security.controller.activities.LoginActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

object SharedMethods {
    fun getJSDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(date)
    }
    fun retrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://apifinanzasbeta.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}