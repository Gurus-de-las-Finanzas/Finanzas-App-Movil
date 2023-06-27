package com.example.finanzas.shared

import android.content.Context
import android.widget.Toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date

object SharedMethods {
    fun getJSDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(date)
    }
    fun retrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://guru-finanzas.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun showToast(context: Context, stringId: Int, time: Int) = Toast.makeText(context, stringId, time).show()
    private fun showToast(context: Context, charSequence: CharSequence, time: Int) = Toast.makeText(context, charSequence, time).show()

    fun showShortToast(context: Context, stringId: Int) { showToast(context, stringId, Toast.LENGTH_SHORT) }
    fun showShortToast(context: Context, charSequence: CharSequence) { showToast(context, charSequence, Toast.LENGTH_SHORT) }

    fun showLongToast(context: Context, stringId: Int) { showToast(context, stringId, Toast.LENGTH_LONG) }
    fun showLongToast(context: Context, charSequence: CharSequence) { showToast(context, charSequence, Toast.LENGTH_LONG) }
}