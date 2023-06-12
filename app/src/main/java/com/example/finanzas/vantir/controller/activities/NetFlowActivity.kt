package com.example.finanzas.vantir.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzas.R
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.StateManager
import com.example.finanzas.vantir.adapter.NetFlowAdapter

class NetFlowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_flow)
        val btnGenVanTir = findViewById<Button>(R.id.btnGenVanTir)
        loadRecyclerView()
        btnGenVanTir.setOnClickListener {
            generateVanTir()
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

    private fun generateVanTir() {
        val intent = Intent(this, VanTirResultsActivity::class.java)
        startActivity(intent)
    }

    private fun loadRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvNetFlows)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NetFlowAdapter(StateManager.netFlowQuantity)
    }
}