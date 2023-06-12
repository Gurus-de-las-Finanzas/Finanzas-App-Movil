package com.example.finanzas.payments.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alclabs.fasttablelayout.FastTableLayout
import com.example.finanzas.R
import com.example.finanzas.clients.adapter.ClientAdapter
import com.example.finanzas.clients.models.Client
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.OnItemClickListener
import com.example.finanzas.shared.StateManager

class PaymentPlanActivity : AppCompatActivity() {
    lateinit var tableLayPaymentPlan: TableLayout
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_plan)
        tableLayPaymentPlan = findViewById(R.id.tableLayPaymentPlan)
        recyclerView = findViewById(R.id.rvClientOwner)
        loadOwner()
        loadTable()
        showDataOnTexts()
    }

    private fun loadOwner() {
        val clientList = arrayListOf(StateManager.selectedClient)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ClientAdapter(clientList, this, false, object: OnItemClickListener<Client> {
            override fun onItemClicked(value: Client) {

            }
        })
    }

    private fun showDataOnTexts() {
        val tvModality = findViewById<TextView>(R.id.tvModality)
        val tvRate = findViewById<TextView>(R.id.tvRate)
        val tvPropertyCost = findViewById<TextView>(R.id.tvPropertyCost)
        val tvPlanLoan = findViewById<TextView>(R.id.tvPlanLoan)
        val tvInitialFee = findViewById<TextView>(R.id.tvInitialFee)
        val tvTerm = findViewById<TextView>(R.id.tvTerm)
        val tvGracePeriodShow = findViewById<TextView>(R.id.tvGracePeriodShow)
        val tvMiViviendaSostenible = findViewById<TextView>(R.id.tvMiViviendaSostenible)
        val tvGoodPayer = findViewById<TextView>(R.id.tvGoodPayer)
        val tvAnnualEffectiveRate = findViewById<TextView>(R.id.tvAnnualEffectiveRate)
        val tvGraceMonthsShow = findViewById<TextView>(R.id.tvGraceMonthsShow)
        val paymentPlan = StateManager.generatedPaymentPlan


        tvModality.text = "Modalidad:"
        tvRate.text = "Tasa de interés: ${paymentPlan.rateType} ${paymentPlan.rate * 100}%"
        tvPropertyCost.text = "Costo de inmueble: ${paymentPlan.propertyCost}"
        tvPlanLoan.text = "Préstamo: ${paymentPlan.loan}"
        tvInitialFee.text = "Cuota inicial: ${paymentPlan.initialFee * 100}%"
        tvTerm.text = "Plazo (años): ${paymentPlan.periodQuantity / 12}"
        tvGracePeriodShow.text = "Periodo de gracia: ${paymentPlan.gracePeriod}"
        tvMiViviendaSostenible.text = "Bono Mi Vivienda Sostenible:"
        tvGoodPayer.text = "Bono buen pagador:"
        tvAnnualEffectiveRate.text = "Tasa efectiva anual:"
        tvGraceMonthsShow.text = "Meses de gracia: ${paymentPlan.graceMonths}"
    }

    private fun loadTable() {
        val headers = arrayOf(
            "Nro",
            "Saldo inicial",
            "Interés",
            "Seguro degr.",
            "Cuota",
            "Amortización",
            "Saldo final")

        val periods = StateManager.generatedPaymentPlan.periods

        var data = Array<Array<String>>(periods.size) { arrayOf() }
        
        periods.forEachIndexed { index, item ->
            data[index] = arrayOf(
                item.numberPeriod.toString(),
                item.initialBalance.toString(),
                item.interest.toString(),
                "",
                item.fee.toString(),
                item.amortization.toString(),
                item.finalBalance.toString()
            )
        }

        val fastTable = FastTableLayout(this, tableLayPaymentPlan, headers, data)

        fastTable.SET_DEAFULT_HEADER_BORDER = true
        fastTable.setCustomBackgroundToHeaders(com.alclabs.fasttablelayout.R.color.purple_500)

        fastTable.build()
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
}