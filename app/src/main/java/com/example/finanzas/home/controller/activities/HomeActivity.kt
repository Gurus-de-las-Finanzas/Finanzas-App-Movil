package com.example.finanzas.home.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.finanzas.R
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
    fun logOut(view: View) {
        AppDatabase.getInstance(this).getLoginCredentialsDao().cleanTable()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}