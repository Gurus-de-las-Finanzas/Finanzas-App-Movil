package com.example.finanzas.clients.controller.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.finanzas.R
import com.example.finanzas.clients.models.Client
import com.example.finanzas.clients.models.SaveClientResource
import com.example.finanzas.clients.network.ClientService
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