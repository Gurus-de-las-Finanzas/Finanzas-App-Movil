package com.example.finanzas.payments.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.finanzas.R
import com.example.finanzas.databinding.ActivityPaymentFormBinding
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.payments.LoanProperties
import com.example.finanzas.payments.enums.Coin
import com.example.finanzas.payments.enums.GracePeriod
import com.example.finanzas.payments.enums.LienTypes
import com.example.finanzas.payments.enums.LoanReason
import com.example.finanzas.payments.enums.TypeRate
import com.example.finanzas.payments.models.SavePaymentPlanResource
import com.example.finanzas.payments.models.SavePeriodResource
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.ExtensionMethods.calculateFee
import com.example.finanzas.shared.ExtensionMethods.capitalize
import com.example.finanzas.shared.ExtensionMethods.convertCoin
import com.example.finanzas.shared.ExtensionMethods.convertEffectiveRate
import com.example.finanzas.shared.ExtensionMethods.getGoodPayerBonus
import com.example.finanzas.shared.ExtensionMethods.getLienType
import com.example.finanzas.shared.ExtensionMethods.getLoanRate
import com.example.finanzas.shared.ExtensionMethods.getRate
import com.example.finanzas.shared.ExtensionMethods.isNone
import com.example.finanzas.shared.ExtensionMethods.isNullOrEmpty
import com.example.finanzas.shared.ExtensionMethods.isWhat
import com.example.finanzas.shared.ExtensionMethods.showShortToast
import com.example.finanzas.shared.ExtensionMethods.toChar
import com.example.finanzas.shared.ExtensionMethods.toTextString
import com.example.finanzas.shared.StateManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentFormActivity : AppCompatActivity() {

    private var selectedRate = TypeRate.EFFECTIVE
    private var gracePeriod = GracePeriod.NONE
    private var coin = Coin.SOLES
    private var loanReason = LoanReason.NONE
    private var lienType = LienTypes.INDIVIDUAL

    var term: Int = 5
    var graceMonths: Int = 0

    private lateinit var lienOptions: Array<String>
    private lateinit var binding: ActivityPaymentFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lienOptions = resources.getStringArray(R.array.lien_types)

        enableSeekBars()
        enableLienTypeSpinner()
        binding.btnGeneratePlan.setOnClickListener {
            if(isValid()) {
                generatePaymentPlan()
                StateManager.paymentFromBack = false
                val intent = Intent(this, PaymentPlanActivity::class.java)
                startActivity(intent)
            }
        }

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

        onRadioButtonClicked(binding.rbtnEffective)
        if(selectedRate.isNone())
            onRadioButtonClicked(binding.rbtnNominal)

        onRadioButtonClicked(binding.rbtnSoles)
        if(coin.isNone())
            onRadioButtonClicked(binding.rbtnDolars)

    }

    private fun notValidAndToast(message: String) = false.also { showShortToast(message) }

    private fun isValid(): Boolean {
        if(loanReason.isNone())
            return notValidAndToast("Por favor, seleccione el motivo del prestamo")

        val needInitialFee = loanReason.isWhat(LoanReason.MORTGAGE_LOAN)

        if((needInitialFee && binding.etInitialFee.isNullOrEmpty()) || binding.etPropertyPrice.isNullOrEmpty())
            return notValidAndToast("Por favor, llene todos los campos")

        val price = binding.etPropertyPrice.toTextString().toDouble()
        var minPrice = LoanProperties.minPrice
        var coin = "soles"

        if(this.coin.isWhat(Coin.DOLLAR)) {
            coin = "dolares"
            minPrice = LoanProperties.minPrice.convertCoin(Coin.DOLLAR)
        }

        if(price < minPrice)
            return notValidAndToast("El precio del inmueble no debe ser menor a $minPrice $coin")
        val maxPrice = if(this.coin.isWhat(Coin.SOLES)) LoanProperties.maxPrice else LoanProperties.maxPrice.convertCoin(
            Coin.DOLLAR)
        if(price > maxPrice)
            return notValidAndToast("El precio del inmueble no debe ser mayor a $maxPrice $coin")

        val initialFee = if(binding.etInitialFee.isNullOrEmpty()) 0.0 else binding.etInitialFee.toTextString().toDouble()
        if(needInitialFee && initialFee < LoanProperties.minInitialFee)
            return notValidAndToast("El pago inicial no debe ser menor a ${LoanProperties.minInitialFee}%")

        if(initialFee > LoanProperties.maxInitialFee)
            return notValidAndToast("El pago inicial no debe ser mayor a ${LoanProperties.maxInitialFee}%")

        if(lienType.isNone())
            return notValidAndToast("Por favor, seleccione el tipo de seguro de desgravamen")

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

        val isSustainable = binding.switchMiViviendaSostenible.isChecked
        val isStateSupport = binding.switchStateSupport.isChecked
        val goodPayerBonus = if(isStateSupport) 0.0 else propertyPrice.getGoodPayerBonus(coin, isSustainable)


        val loan = (propertyPrice * (1 - initialFee)) - goodPayerBonus
        var fee: Double
        val periodQuantity = term * 12
        //val periodQuantity = 8

        val rate = when(selectedRate) {
            //en los dos es efectiva por ahora
            TypeRate.NOMINAL -> loan.getLoanRate(coin)
            TypeRate.EFFECTIVE -> loan.getLoanRate(coin)
            else -> 0.0
        }
        val periods = mutableListOf<SavePeriodResource>()
        var initialBalance: Double
        var interest: Double
        var amortization: Double
        var finalBalance: Double
        var finalFee: Double
        var lien: Double

        val effectiveRate = rate.convertEffectiveRate(30)
        val lienRate = lienType.getRate()
        var completeLian = 0.0

        for (i in 0 until periodQuantity) {
            val isInGraceMonth = i < graceMonths
            initialBalance = if (i == 0) loan else periods[i - 1].finalBalance
            interest = initialBalance * effectiveRate
            lien = initialBalance * lienRate
            fee = initialBalance.calculateFee(effectiveRate, periodQuantity, i + 1, lienRate)
            amortization = if (isInGraceMonth) 0.0 else fee - interest - lien
            completeLian += lien
            if (isInGraceMonth) {
                finalBalance = if(gracePeriod == GracePeriod.TOTAL) initialBalance + interest + lien else initialBalance
                finalFee = if(gracePeriod == GracePeriod.PARTIAL) interest + lien else 0.0
            }
            else {
                finalBalance = initialBalance - amortization
                finalFee = fee
            }

            periods.add(SavePeriodResource(
                numberPeriod =  i + 1,
                initialBalance =  initialBalance,
                interest = interest, amortization =  amortization,
                fee =  finalFee,
                finalBalance = finalBalance,
                lienInsurance = lien,
                propertyInsurance = 0.0,
                scheduleId = 0
            ))
        }

        val sustainableBonus = if(coin.isWhat(Coin.DOLLAR)) LoanProperties.GoodPayerBonus.sustainableBonus.convertCoin(
            Coin.DOLLAR) else LoanProperties.GoodPayerBonus.sustainableBonus

        StateManager.generatedPaymentPlan =
            SavePaymentPlanResource(
                coin = coin.toChar(),
                periods = periodQuantity,
                typeRate = selectedRate.toChar(),
                interestRate = rate, propertyCost = propertyPrice,
                graceMonths = graceMonths,
                initialFeePercent = initialFee, gracePeriod = gracePeriod.toChar(),
                loan = loan,
                goodPayerBonus = if(isSustainable && !isStateSupport && goodPayerBonus != 0.0) goodPayerBonus - sustainableBonus else goodPayerBonus,
                miViviendaBonus = if(isSustainable && !isStateSupport && goodPayerBonus != 0.0) sustainableBonus else 0.0,
                modality = lienType.capitalize(),
                name = StateManager.selectedClient.name,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                typePeriod = "mensual",
                clientId = StateManager.selectedClient.id
            )
        StateManager.periods = periods
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
            binding.spinnerLienType.adapter = adapter
        }
        binding.spinnerLienType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val capitalized = lienOptions[position].replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
                lienType = capitalized.getLienType()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                lienType = LienTypes.NONE
            }
        }
    }

    private fun enableSeekBars() {
        binding.seekBarGraceMonths.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 1)
                    binding.tvSelectedGraceMonths.text = "1 mes"
                else binding.tvSelectedGraceMonths.text = "$progress meses"
                graceMonths = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        binding.seekBarTermYears.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress < LoanProperties.minYears) {
                    seekBar?.progress = LoanProperties.minYears
                    return
                }
                binding.tvSelectedTerm.text = "$progress años"
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