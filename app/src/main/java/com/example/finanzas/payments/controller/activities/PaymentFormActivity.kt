package com.example.finanzas.payments.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.example.finanzas.R
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.payments.models.SavePaymentPlanResource
import com.example.finanzas.payments.models.SavePeriodResource
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.StateManager
import kotlin.math.pow

class PaymentFormActivity : AppCompatActivity() {
    lateinit var seekBarTermYears: SeekBar
    lateinit var seekBarGraceMonths: SeekBar
    lateinit var tvSelectedTerm: TextView
    lateinit var tvSelectedGraceMonths: TextView
    lateinit var spinnerLienType: Spinner
    lateinit var selectedRate: String
    lateinit var selectedLien: String
    var gracePeriod: Char = '\u0000'
    var coin: Char = '\u0000'
    var term: Int = 5
    var graceMonths: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_form)
        seekBarTermYears = findViewById(R.id.seekBarTermYears)
        seekBarGraceMonths = findViewById(R.id.seekBarGraceMonths)
        tvSelectedTerm = findViewById(R.id.tvSelectedTerm)
        tvSelectedGraceMonths = findViewById(R.id.tvSelectedGraceMonths)
        spinnerLienType = findViewById(R.id.spinnerLienType)
        val btnGeneratePlan = findViewById<Button>(R.id.btnGeneratePlan)
        enableSeekBars()
        enableLienTypeSpinner()
        btnGeneratePlan.setOnClickListener {
            generatePaymentPlan()
            val intent = Intent(this, PaymentPlanActivity::class.java)
            startActivity(intent)
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.rbtnNominal ->
                    if (checked) {
                        println("Seleccionado nominal")
                        selectedRate = "Nominal"
                    }
                R.id.rbtnEffective ->
                    if (checked) {
                        println("Seleccionado efectiva")
                        selectedRate = "Effective"
                    }
                R.id.rbtnSoles ->
                    if (checked) {
                        println("Seleccionado soles")
                        coin = 'S'
                    }
                R.id.rbtnDolars ->
                    if (checked) {
                        println("Seleccionado dolares")
                        coin = 'D'
                    }
                R.id.rbtnGraceTotal ->
                    if (checked) {
                        println("Seleccionado gracia total")
                        gracePeriod = 'T'
                    }
                R.id.rbtnGracePartial ->
                    if (checked) {
                        println("Seleccionado gracia parcial")
                        gracePeriod = 'P'
                    }
            }
        }
    }

    private fun generatePaymentPlan() {
        val etPropertyPrice = findViewById<EditText>(R.id.etPropertyPrice)
        val etInitialFee = findViewById<EditText>(R.id.etInitialFee)
        var initialFee = 0.0
        if (etInitialFee.text.isNotBlank())
            initialFee = etInitialFee.text.toString().toDouble() / 100.0
        val propertyPrice = etPropertyPrice.text.toString().toDouble()
        val loan = propertyPrice * (1 - initialFee)
        var fee = 0.0
        val periodQuantity = term * 12
        //val periodQuantity = 8
        val hasGraceMonths = graceMonths > 0
        var rate = 0.0
        when(selectedRate) {
            //en los dos es efectiva por ahora
            "Nominal" -> {
                //rate = 0.1399
                rate = 0.044030651
                val roundFee = String.format("%.2f", loan * ((rate * (1 + rate).pow(periodQuantity))/((1 + rate).pow(periodQuantity) - 1)))
                fee = roundFee.toDouble()
            }
            "Effective" -> {
                //rate = 0.1399
                rate = 0.044030651
                fee = String.format("%.2f", loan * ((rate * (1 + rate).pow(periodQuantity))/((1 + rate).pow(periodQuantity) - 1))).toDouble()
            }
        }
        val periods = mutableListOf<SavePeriodResource>()
        var initialBalance: Double
        var interest: Double
        var amortization: Double
        var finalBalance: Double
        var finalFee: Double
        for (i in 0 until periodQuantity) {
            if (i == 0) initialBalance = loan
            else initialBalance = periods[i - 1].finalBalance
            interest = String.format("%.2f", initialBalance * rate).toDouble()

            if (hasGraceMonths && i < graceMonths)
                amortization = 0.0
            else amortization = String.format("%.2f", fee - interest).toDouble()

            if (hasGraceMonths && gracePeriod == 'T' && i == 0) {
                finalBalance = String.format("%.2f", initialBalance + interest).toDouble()
                finalFee = 0.0
            }
            else {
                finalBalance = String.format("%.2f", initialBalance - amortization).toDouble()
                finalFee = fee
            }

            periods.add(SavePeriodResource(i + 1, initialBalance, interest, amortization, finalFee, finalBalance))
        }
        StateManager.generatedPaymentPlan =
            SavePaymentPlanResource(coin, periodQuantity, selectedRate, rate, propertyPrice, periods, graceMonths, initialFee, gracePeriod, loan, StateManager.selectedClient.id)
    }

    private fun enableLienTypeSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.lien_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerLienType.adapter = adapter
        }
        spinnerLienType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val options = resources.getStringArray(R.array.lien_types)
                println(options[position])
                selectedLien = options[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLien = "Ninguno"
            }
        }
    }

    private fun enableSeekBars() {
        seekBarGraceMonths.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 1)
                    tvSelectedGraceMonths.text = "1 mes"
                else tvSelectedGraceMonths.text = "$progress meses"
                graceMonths = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        seekBarTermYears.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 1)
                    tvSelectedTerm.text = "1 año"
                tvSelectedTerm.text = "$progress años"
                term = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
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