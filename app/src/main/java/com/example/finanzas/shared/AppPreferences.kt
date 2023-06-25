package com.example.finanzas.shared

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finanzas.security.models.User
import com.google.gson.Gson

class AppPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    companion object {
        const val NAME = "preferences"
        const val USER = "USER"
        const val TOKEN = "TOKEN"
        val AppCompatActivity.preferences: AppPreferences get() = AppPreferences(this)
        val Fragment.preferences: AppPreferences get() = AppPreferences(requireContext())
    }

    private fun userToString(user: User): String = gson.toJson(user)
    private fun getUserString(): String = preferences.getString(USER, userToString(defaultUser()))!!
    private fun defaultUser(): User = User(0, "", "", 0, null, "")
    private fun defaultToken() = ""
    private fun editor(): SharedPreferences.Editor = preferences.edit()

    fun saveUser(user: User) {
        editor().apply {
            putString(USER, userToString(user))
            commit()
        }
    }
    fun getUser(): User = gson.fromJson(getUserString(), User::class.java)
    fun clean() = saveUser(defaultUser())
    fun isDefault() = getUser().id == 0 ||getToken() == ""
    fun isNotDefault() = !isDefault()

    fun getToken() = preferences.getString(TOKEN, defaultToken())!!

    fun saveToken(token: String) {
        editor().apply {
            putString(TOKEN, token)
            commit()
        }
    }


}