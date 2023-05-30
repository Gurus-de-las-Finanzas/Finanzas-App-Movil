package com.example.finanzas.clients.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.finanzas.R
import com.example.finanzas.clients.models.Client
import com.example.finanzas.clients.models.SaveClientResource
import com.example.finanzas.clients.network.ClientService
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.SharedMethods
import com.example.finanzas.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class AddClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)
        val btnAddNewClient = findViewById<Button>(R.id.btnAddNewClient)
        btnAddNewClient.setOnClickListener {
            addClient()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.itemHome -> {
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                true
            }
            R.id.itemLogOut -> {
                AppDatabase.getInstance(this).getLoginCredentialsDao().cleanTable()
                val intent = Intent(this, LoginActivity::class.java)
                //cerrar todos los activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addClient() {
        val etClientName = findViewById<EditText>(R.id.etClientName)
        val etClientLastName = findViewById<EditText>(R.id.etClientLastName)
        val etClientDNI = findViewById<EditText>(R.id.etClientDNI)

        if (etClientName.text.isNotBlank() && etClientName.length() <= 50 &&
            etClientLastName.text.isNotBlank() && etClientLastName.length() <= 50 &&
            etClientDNI.text.isNotBlank() && etClientDNI.length() == 8) {
            val retrofit = SharedMethods.retrofitBuilder()
            val clientService = retrofit.create(ClientService::class.java)
            val request = clientService.saveClient(StateManager.authToken,
                SaveClientResource(
                    etClientName.text.toString(),
                    etClientLastName.text.toString(),
                    etClientDNI.text.toString(),
                    StateManager.loggedUserId)
            )
            request.enqueue(object: Callback<Client> {
                override fun onResponse(call: Call<Client>, response: Response<Client>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddClientActivity, "Cliente agregado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else
                        Toast.makeText(this@AddClientActivity, "Error al agregar cliente: ${response.message()}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Client>, t: Throwable) {
                    Toast.makeText(this@AddClientActivity, "Error al agregar cliente: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
        else
            Toast.makeText(this, "Error de validacion", Toast.LENGTH_SHORT).show()
    }
}