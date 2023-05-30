package com.example.finanzas.clients.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzas.R
import com.example.finanzas.clients.adapter.ClientAdapter
import com.example.finanzas.clients.models.Client
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

class ClientsActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    var clients: List<Client> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)
        val btnAddClient = findViewById<ImageButton>(R.id.btnAddClient)
        recyclerView = findViewById(R.id.rvClients)
        loadClients()
        btnAddClient.setOnClickListener {
            goToAddClientActivity()
        }
    }

    private fun goToAddClientActivity() {
        val intent = Intent(this, AddClientActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadClients()
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

    private fun loadClients() {
        val token = StateManager.authToken
        val retrofit = SharedMethods.retrofitBuilder()

        val clientService = retrofit.create(ClientService::class.java)
        val request = clientService.getClientsByUserId(token, StateManager.loggedUserId)

        request.enqueue(object : Callback<List<Client>> {
            override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                if (response.isSuccessful) {
                    clients = response.body()!!
                    recyclerView.layoutManager = LinearLayoutManager(this@ClientsActivity)
                    recyclerView.adapter = ClientAdapter(clients, this@ClientsActivity)
                }
                else
                    Toast.makeText(this@ClientsActivity, "Error al obtener clientes: ${response.message()}", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                Toast.makeText(this@ClientsActivity, "Error al obtener clientes: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}