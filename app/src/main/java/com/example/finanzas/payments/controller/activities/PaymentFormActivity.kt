package com.example.finanzas.payments.controller.activities

import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import com.example.finanzas.R
import com.example.finanzas.databinding.ActivityPaymentFormBinding
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.payments.models.SavePaymentPlanResource
import com.example.finanzas.payments.models.SavePeriodResource
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.ExtensionMethods.calculateFee
import com.example.finanzas.shared.ExtensionMethods.convertCoin
import com.example.finanzas.shared.ExtensionMethods.isNone
import com.example.finanzas.shared.ExtensionMethods.isNullOrEmpty
import com.example.finanzas.shared.ExtensionMethods.name
import com.example.finanzas.shared.ExtensionMethods.showShortToast
import com.example.finanzas.shared.ExtensionMethods.toChar
import com.example.finanzas.shared.ExtensionMethods.toTextString
import com.example.finanzas.shared.LoanProperties
import com.example.finanzas.shared.StateManager
import com.example.finanzas.shared.enums.Coin
import com.example.finanzas.shared.enums.GracePeriod
import com.example.finanzas.shared.enums.LoanReason
import com.example.finanzas.shared.enums.TypeRate

class PaymentFormActivity : AppCompatActivity() {
    lateinit var seekBarTermYears: SeekBar
    lateinit var seekBarGraceMonths: SeekBar
    lateinit var tvSelectedTerm: TextView
    lateinit var tvSelectedGraceMonths: TextView
    lateinit var spinnerLienType: Spinner
    lateinit var selectedLien: String
    private lateinit var binding: ActivityPaymentFormBinding
    private var selectedRate = TypeRate.EFFECTIVE
    private var gracePeriod = GracePeriod.NONE
    private var coin = Coin.SOLES
    private var loanReason = LoanReason.NONE

    var term: Int = 5
    var graceMonths: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        seekBarTermYears = findViewById(R.id.seekBarTermYears)
        seekBarGraceMonths = findViewById(R.id.seekBarGraceMonths)
        tvSelectedTerm = findViewById(R.id.tvSelectedTerm)
        tvSelectedGraceMonths = findViewById(R.id.tvSelectedGraceMonths)
        spinnerLienType = findViewById(R.id.spinnerLienType)
        val btnGeneratePlan = findViewById<Button>(R.id.btnGeneratePlan)

        enableSeekBars()
        enableLienTypeSpinner()
        btnGeneratePlan.setOnClickListener {
            if(isValid()) {
                generatePaymentPlan()
                val intent = Intent(this, PaymentPlanActivity::class.java)
                startActivity(intent)
            }
        }
        binding.rbtnSoles.isChecked = true
        binding.rbtnEffective.isChecked = true
    }

    override fun onResume() {
        super.onResume()


        if(loanReason.isNone())
            onRadioButtonClicked(binding.rbtnConstruction)
        if(loanReason.isNone())
            onRadioButtonClicked(binding.rbtnImprovements)
        if(loanReason.isNone())
            onRadioButtonClicked(binding.rbtnMortgage)

        if(gracePeriod.isNone())
            onRadioButtonClicked(binding.rbtnGracePartial)
        if(gracePeriod.isNone())
            onRadioButtonClicked(binding.rbtnGraceTotal)

        if(selectedRate.isNone())
            onRadioButtonClicked(binding.rbtnEffective)
        if(selectedRate.isNone())
            onRadioButtonClicked(binding.rbtnNominal)

        if(coin.isNone())
            onRadioButtonClicked(binding.rbtnSoles)
        if(coin.isNone())
            onRadioButtonClicked(binding.rbtnDolars)
    }

    private fun notValidAndToast(message: String) = false.also { showShortToast(message) }

    private fun isValid(): Boolean {
        if(loanReason == LoanReason.NONE)
            return notValidAndToast("Por favor, seleccione el motivo del prestamo")

        if(binding.etInitialFee.isNullOrEmpty() || binding.etPropertyPrice.isNullOrEmpty())
            return notValidAndToast("Por favor, llene todos los campos")

        val price = binding.etPropertyPrice.toTextString().toDouble()
        var minPrice = LoanProperties.minPrice
        var coin = "soles"

        if(this.coin == Coin.DOLLAR) {
            coin = "dolares"
            minPrice = LoanProperties.minPrice.convertCoin(Coin.DOLLAR)
        }

        if(price < minPrice)
            return notValidAndToast("El precio del inmueble no debe ser menor a $minPrice $coin")
        val maxPrice = if(this.coin == Coin.SOLES) LoanProperties.maxPrice else LoanProperties.maxPrice.convertCoin(Coin.DOLLAR)
        if(price > maxPrice)
            return notValidAndToast("El precio del inmueble no debe ser mayor a $maxPrice $coin")

        val initialFee = binding.etInitialFee.toTextString().toDouble()
        if(initialFee < LoanProperties.minInitialFee)
            return notValidAndToast("El pago inicial no debe ser menor a ${LoanProperties.minInitialFee}%")

        if(initialFee > LoanProperties.maxInitialFee)
            return notValidAndToast("El pago inicial no debe ser mayor a ${LoanProperties.maxInitialFee}%")

        if(binding.seekBarGraceMonths.progress > 0 && gracePeriod.isNone())
            return notValidAndToast("Por favor, seleccione el tipo de periodo de gracia")


        return true
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.rbtnNominal -> selectedRate = if(checked) TypeRate.NOMINAL else TypeRate.NONE
                R.id.rbtnEffective -> selectedRate = if(checked) TypeRate.EFFECTIVE else TypeRate.NONE
                R.id.rbtnSoles -> coin = if(checked) Coin.SOLES else Coin.NONE
                R.id.rbtnDolars -> coin = if(checked) Coin.DOLLAR else Coin.NONE
                R.id.rbtnGraceTotal -> gracePeriod = if(checked) GracePeriod.TOTAL else GracePeriod.NONE
                R.id.rbtnGracePartial -> gracePeriod = if(checked) GracePeriod.PARTIAL else GracePeriod.NONE
                R.id.rbtnConstruction -> loanReason = if(checked) LoanReason.CONSTRUCTION else LoanReason.NONE
                R.id.rbtnMortgage -> loanReason = if(checked) LoanReason.MORTGAGE_LOAN else LoanReason.NONE
                R.id.rbtnImprovements -> loanReason = if (checked) LoanReason.IMPROVEMENTS_OR_EXTENSIONS else LoanReason.NONE
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
        var fee: Double
        val periodQuantity = term * 12
        //val periodQuantity = 8

        val rate = when(selectedRate) {
            //en los dos es efectiva por ahora
            TypeRate.NOMINAL -> {
                //0.1399
                0.044030651
            }
            TypeRate.EFFECTIVE -> {
                //0.1399
                0.044030651
            }

            else -> 0.0
        }
        val periods = mutableListOf<SavePeriodResource>()
        var initialBalance: Double
        var interest: Double
        var amortization: Double
        var finalBalance: Double
        var finalFee: Double
        for (i in 0 until periodQuantity) {
            val isInGraceMonth = i < graceMonths
            initialBalance = if (i == 0) loan else periods[i - 1].finalBalance
            interest = initialBalance * rate
            fee = initialBalance.calculateFee(rate, periodQuantity, i + 1)
            amortization = if (isInGraceMonth && i < graceMonths) 0.0 else fee - interest

            if (isInGraceMonth) {
                finalBalance = if(gracePeriod == GracePeriod.TOTAL) initialBalance + interest else initialBalance
                finalFee = if(gracePeriod == GracePeriod.PARTIAL) interest else 0.0
            }
            else {
                finalBalance = initialBalance - amortization
                finalFee = fee
            }

            periods.add(SavePeriodResource(i + 1, initialBalance, interest, amortization, finalFee, finalBalance))
        }
        StateManager.generatedPaymentPlan =
            SavePaymentPlanResource(
                coin.toChar(),
                periodQuantity,
                selectedRate.name(),
                rate, propertyPrice,
                periods, graceMonths,
                initialFee, gracePeriod.toChar(),
                loan, StateManager.selectedClient.id
            )
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
                if(progress < LoanProperties.minYears) {
                    seekBar?.progress = LoanProperties.minYears
                    return
                }
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