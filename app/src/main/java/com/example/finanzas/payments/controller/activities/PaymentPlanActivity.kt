package com.example.finanzas.payments.controller.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanzas.R
import com.example.finanzas.clients.adapter.ClientAdapter
import com.example.finanzas.databinding.ActivityPaymentPlanBinding
import com.example.finanzas.databinding.DialogSavePlanBinding
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.payments.models.PaymentPlan
import com.example.finanzas.payments.network.PeriodService
import com.example.finanzas.payments.network.PlanService
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.AppPreferences.Companion.preferences
import com.example.finanzas.shared.ExtensionMethods.firstName
import com.example.finanzas.shared.ExtensionMethods.getCoin
import com.example.finanzas.shared.ExtensionMethods.getGracePeriod
import com.example.finanzas.shared.ExtensionMethods.getRate
import com.example.finanzas.shared.ExtensionMethods.isEven
import com.example.finanzas.shared.ExtensionMethods.putPercent
import com.example.finanzas.shared.ExtensionMethods.round
import com.example.finanzas.shared.ExtensionMethods.showShortToast
import com.example.finanzas.shared.ExtensionMethods.startActivityAndClean
import com.example.finanzas.shared.ExtensionMethods.toDP
import com.example.finanzas.shared.ExtensionMethods.toPercentage
import com.example.finanzas.shared.SharedMethods
import com.example.finanzas.shared.StateManager
import com.example.finanzas.shared.models.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentPlanBinding
    private lateinit var savePlanDialog: AlertDialog
    private lateinit var dialogBinding: DialogSavePlanBinding
    private val retrofit = SharedMethods.retrofitBuilder()
    private val planService: PlanService = retrofit.create(PlanService::class.java)
    private val periodService: PeriodService = retrofit.create(PeriodService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentPlanBinding.inflate(layoutInflater)
        dialogBinding = DialogSavePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadOwner()
        loadTable()
        showDataOnTexts()
        initDialog()
    }

    override fun onResume() {
        super.onResume()
        onResumeDialog()

    }

    private fun onResumeDialog() {
        var text = getString(R.string.save_plan)
        var id = R.drawable.ic_save
        if(StateManager.paymentFromBack)
        {
            text = getString(R.string.delete_plan)
            id = R.drawable.ic_delete
        }
        binding.ibSavePlan.setImageResource(id)
        dialogBinding.tvSavePlan.text = text.format(StateManager.selectedClient.firstName())
        dialogBinding.btSavePlan.text = getString(if(StateManager.paymentFromBack) R.string.delete else R.string.save)
    }

    private fun initDialog() {
        dialogBinding.btCancel.setOnClickListener { savePlanDialog.dismiss() }
        dialogBinding.btSavePlan.setOnClickListener { saveOrDeletePlan() }
        binding.ibSavePlan.setOnClickListener { savePlanDialog.show() }

        savePlanDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        savePlanDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_save_plan)
    }

    private fun endDialogAction(isOk: Boolean) = if(isOk) startActivityAndClean(HomeActivity::class.java) else savePlanDialog.dismiss()

    private fun showErrorException(t: Throwable) {
        showShortToast("Ocurrió un error ${t.message}")
        endDialogAction(false)
    }

    private fun savePeriods() {

        periodService.saveManyPeriods(preferences.getToken(), StateManager.periods)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    showShortToast(if(response.isSuccessful) "Plan guardado exitosamente" else "Ocurrio algo inesperado, el plan no se ha guardado")
                    endDialogAction(response.isSuccessful)
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) = showErrorException(t)

            })
    }

    private fun saveOrDeletePlan() {
        if(StateManager.paymentFromBack) {
           planService.deletePlan(preferences.getToken(), StateManager.paymentFromBackId)
               .enqueue(object : Callback<PaymentPlan> {
                   override fun onResponse(
                       call: Call<PaymentPlan>,
                       response: Response<PaymentPlan>
                   ) {
                       showShortToast(if(response.isSuccessful) "Plan borrado exitosamente" else "Ocurrio algo inesperado, el plan no se ha borrado")
                       endDialogAction(response.isSuccessful)
                   }

                   override fun onFailure(call: Call<PaymentPlan>, t: Throwable) = showErrorException(t)

               })
        }
        else {
            planService.savePlan(preferences.getToken(), StateManager.generatedPaymentPlan)
                .enqueue(object : Callback<PaymentPlan> {
                    override fun onResponse(
                        call: Call<PaymentPlan>,
                        response: Response<PaymentPlan>
                    ) {
                        if(response.isSuccessful){
                            response.body().let {plan ->
                                if(plan != null){
                                    StateManager.periods.forEach {
                                        it.scheduleId = plan.id
                                    }
                                    savePeriods()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<PaymentPlan>, t: Throwable) = showErrorException(t)

                })
        }
    }


    private fun loadOwner() {
        val clientList = arrayListOf(StateManager.selectedClient)
        binding.rvClientOwner.layoutManager = LinearLayoutManager(this)
        binding.rvClientOwner.adapter = ClientAdapter(clientList, false)
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

        binding.tableLayPaymentPlan.addView(tableRow)
    }

    private fun showDataOnTexts() {
        val paymentPlan = StateManager.generatedPaymentPlan

        binding.tvModality.text = getString(R.string.modality).format(paymentPlan.modality)
        binding.tvRate.text = getString(R.string.rate).format(paymentPlan.typeRate.getRate(), paymentPlan.interestRate.toPercentage()).putPercent()
        binding.tvPropertyCost.text = getString(R.string.property_cost).format(paymentPlan.propertyCost)
        binding.tvPlanLoan.text = getString(R.string.loan).format(paymentPlan.loan)
        binding.tvInitialFee.text = getString(R.string.initial_fee).format(paymentPlan.initialFeePercent.toPercentage()).putPercent()
        binding.tvTerm.text = getString(R.string.terms).format(paymentPlan.periods / 12)
        binding.tvGracePeriodShow.text = getString(R.string.grace_period).format(paymentPlan.gracePeriod.getGracePeriod())
        binding.tvMiViviendaSostenible.text = getString(R.string.sustainable_bonus).format(paymentPlan.miViviendaBonus)
        binding.tvGoodPayer.text = getString(R.string.good_payer_bonus).format(paymentPlan.goodPayerBonus)
        binding.tvAnnualEffectiveRate.text = getString(R.string.rate_effective).format(paymentPlan.interestRate.toPercentage()).putPercent()
        binding.tvGraceMonthsShow.text = getString(R.string.grace_moths).format(paymentPlan.graceMonths)
        binding.tvCoinType.text = getString(R.string.coin).format(paymentPlan.coin.getCoin())
    }

    private fun loadTable() {
        addRow(resources.getStringArray(R.array.plan_headers), evenRow = false, areHeader = true)
        StateManager.periods.forEachIndexed  { index, item ->
            addRow(arrayOf(
                item.numberPeriod.toString(),
                item.initialBalance.round(2).toString(),
                item.interest.round(2).toString(),
                item.lienInsurance.round(2).toString(),
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