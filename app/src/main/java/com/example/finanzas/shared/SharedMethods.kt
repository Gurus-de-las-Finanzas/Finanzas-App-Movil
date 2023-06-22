package com.example.finanzas.shared

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.finanzas.security.controller.activities.LoginActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.pow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

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

    private fun showToast(context: Context, stringId: Int, time: Int) = Toast.makeText(context, stringId, time).show()
    fun showShortToast(context: Context, stringId: Int) = showToast(context, stringId, Toast.LENGTH_SHORT)
    fun showLongToast(context: Context, stringId: Int) = showToast(context, stringId, Toast.LENGTH_LONG)
    fun toPercentage(number: Double) = number * 100
    fun round(number: Double, decimals: Int): Double {
        val power = 10.0.pow(decimals);
        return (number * power).roundToInt() / power
    }
}