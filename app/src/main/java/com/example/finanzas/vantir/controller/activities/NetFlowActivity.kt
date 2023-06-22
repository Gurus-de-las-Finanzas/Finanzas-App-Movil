package com.example.finanzas.vantir.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzas.R
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.SharedMethods
import com.example.finanzas.shared.SharedMethods.showShortToast
import com.example.finanzas.shared.StateManager
import com.example.finanzas.vantir.adapter.NetFlowAdapter
import com.example.finanzas.vantir.interfaces.OnTextChangedListener
import com.example.finanzas.vantir.models.VanData
import org.apache.poi.ss.formula.functions.Irr
import org.apache.poi.ss.formula.functions.Irr.irr
import kotlin.math.pow

class NetFlowActivity : AppCompatActivity() {
    lateinit var flows: MutableList<Double>
    lateinit var vanData: VanData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_flow)
        vanData = StateManager.vanData
        val btnGenVanTir = findViewById<Button>(R.id.btnGenVanTir)
        flows = mutableListOf()
        for (i in 0 until vanData.years) {
            flows.add(0.0)
        }
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

    private fun calculateVan() {
        val opportunityCost = vanData.opportunityCost / 100
        var sum = 0.0
        for (i in 0 until vanData.years) {
            sum += String.format("%.2f",(flows[i] / (1 + opportunityCost).pow(i + 1))).toDouble()
        }

        StateManager.vanResult = String.format("%.2f", (-vanData.inversion) + sum).toDouble()
    }

    private fun calculateTir() {
        flows.add(0, -vanData.inversion)
        val array = flows.toDoubleArray()
        StateManager.tirResult = irr(array, 0.01)

    }

    private fun generateVanTir() {
        calculateVan()
        calculateTir()
        val intent = Intent(this, VanTirResultsActivity::class.java)
        startActivity(intent)
    }

    private fun loadRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvNetFlows)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NetFlowAdapter(vanData.years, object : OnTextChangedListener {
            override fun onTextChanged(text: String, position: Int) {
                if(text.isEmpty())
                    flows[position] = 0.0
                else flows[position] = text.toDouble()
            }

        })
    }
}