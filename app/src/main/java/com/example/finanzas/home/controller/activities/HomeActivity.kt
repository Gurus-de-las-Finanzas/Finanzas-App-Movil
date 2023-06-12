package com.example.finanzas.home.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.finanzas.R
import com.example.finanzas.clients.controller.activities.ClientsActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.StateManager
import com.example.finanzas.vantir.controller.activities.VanTirFormActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val btnForm = findViewById<Button>(R.id.btnForm)
        val btnClients = findViewById<Button>(R.id.btnClients)
        val btnVanTir = findViewById<Button>(R.id.btnVanTir)
        btnClients.setOnClickListener {
            goToClientsActivity()
        }
        btnForm.setOnClickListener {
            goToSelectClientFormActivity()
        }
        btnVanTir.setOnClickListener {
            goToVanTirFormActivity()
        }
    }

    private fun goToVanTirFormActivity() {
        val intent = Intent(this, VanTirFormActivity::class.java)
        startActivity(intent)
    }

    private fun goToSelectClientFormActivity() {
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