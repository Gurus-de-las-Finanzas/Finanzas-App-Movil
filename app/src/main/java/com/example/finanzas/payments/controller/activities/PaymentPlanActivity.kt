package com.example.finanzas.payments.controller.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzas.R
import com.example.finanzas.clients.adapter.ClientAdapter
import com.example.finanzas.clients.models.Client
import com.example.finanzas.databinding.ActivityPaymentPlanBinding
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.ExtensionMethods.isEven
import com.example.finanzas.shared.ExtensionMethods.round
import com.example.finanzas.shared.ExtensionMethods.toDP
import com.example.finanzas.shared.OnItemClickListener
import com.example.finanzas.shared.StateManager

class PaymentPlanActivity : AppCompatActivity() {
    lateinit var tableLayPaymentPlan: TableLayout
    lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityPaymentPlanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tableLayPaymentPlan = binding.tableLayPaymentPlan
        recyclerView = binding.rvClientOwner
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

    private fun addRow(rows: Array<String>, evenRow: Boolean,
                       areHeader: Boolean = false, hasGracePeriod: Boolean = false) {
        val tableRow = TableRow(this)

        rows.forEachIndexed { index, s ->
            val textView = TextView(this)

            textView.apply {
                text = s
                gravity = Gravity.CENTER
                background = AppCompatResources.getDrawable(this@PaymentPlanActivity, R.drawable.bg_table_column)
            }
            tableRow.addView(textView)

            var dp = 5.toDP(this@PaymentPlanActivity)
            if(areHeader){
                dp = 10.toDP(this@PaymentPlanActivity)
                tableRow.setBackgroundColor(getColor(R.color.purple_500))
                textView.setTextColor(Color.WHITE)
                textView.setTypeface(null, Typeface.BOLD)
            }
            else {
                textView.setTextColor(getColor(R.color.number_row))
                if (index == 2)
                    textView.setTextColor(getColor(R.color.interest))
                if (index == 5)
                    textView.setTextColor(Color.RED)

                if(s.toDoubleOrNull() == 0.0 && index == 6)
                    textView.setBackgroundColor(Color.GREEN)

                if(hasGracePeriod) {
                    if(index in 4 .. 5)
                        textView.setTextColor(getColor(R.color.purple_200))
                }
            }

            textView.setPadding(dp, 0, dp, 0)

        }

        if (evenRow)
            tableRow.setBackgroundColor(getColor(R.color.even_row))

        tableLayPaymentPlan.addView(tableRow)
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
        addRow(resources.getStringArray(R.array.plan_headers), evenRow = false, areHeader = true)
        StateManager.generatedPaymentPlan.periods.forEachIndexed  { index, item ->
            addRow(arrayOf(
                item.numberPeriod.toString(),
                item.initialBalance.round(2).toString(),
                item.interest.round(2).toString(),
                "",
                item.fee.round(2).toString(),
                item.amortization.round(2).toString(),
                item.finalBalance.round(2).toString()
            ), index.isEven(), hasGracePeriod = item.amortization == 0.0)
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
}