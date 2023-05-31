package com.example.finanzas.home.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.example.finanzas.R
import com.example.finanzas.clients.controller.activities.ClientsActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.StateManager

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val btnForm = findViewById<Button>(R.id.btnForm)
        val btnClients = findViewById<Button>(R.id.btnClients)
        btnClients.setOnClickListener {
            goToClientsActivity()
        }
        btnForm.setOnClickListener {
            goToSelectClientForFormActivity()
        }
    }

    private fun goToSelectClientForFormActivity() {
        val intent = Intent(this, ClientsActivity::class.java)
        startActivity(intent)
        StateManager.addClientButtonActivated = false
        StateManager.frenchButtonActivated = true
    }

    private fun goToClientsActivity() {
        val intent = Intent(this, ClientsActivity::class.java)
        startActivity(intent)
        StateManager.addClientButtonActivated = true
        StateManager.frenchButtonActivated = false
    }

    fun logOut(view: View) {
        AppDatabase.getInstance(this).getLoginCredentialsDao().cleanTable()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}