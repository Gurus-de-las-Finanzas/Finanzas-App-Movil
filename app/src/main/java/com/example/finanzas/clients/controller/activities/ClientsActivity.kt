package com.example.finanzas.clients.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finanzas.R
import com.example.finanzas.clients.adapter.ClientAdapter
import com.example.finanzas.clients.models.Client
import com.example.finanzas.clients.network.ClientService
import com.example.finanzas.databinding.ActivityClientsBinding
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.payments.controller.activities.PaymentFormActivity
import com.example.finanzas.payments.controller.activities.PaymentPlanActivity
import com.example.finanzas.payments.models.PaymentPlan
import com.example.finanzas.payments.models.Period
import com.example.finanzas.payments.network.PlanService
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.AppPreferences.Companion.preferences
import com.example.finanzas.shared.ExtensionMethods.showShortToast
import com.example.finanzas.shared.ExtensionMethods.toSavePaymentPlan
import com.example.finanzas.shared.ExtensionMethods.toSavePeriod
import com.example.finanzas.shared.OnItemClickListener
import com.example.finanzas.shared.SharedMethods
import com.example.finanzas.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientsActivity : AppCompatActivity(), OnItemClickListener<Client>, ClientAdapter.OnClientDeleteListener {
    var clients: ArrayList<Client> = ArrayList()
    private val retrofit = SharedMethods.retrofitBuilder()
    private var clientService: ClientService = retrofit.create(ClientService::class.java)
    private var planService: PlanService = retrofit.create(PlanService::class.java)
    private lateinit var binding: ActivityClientsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnAddClient = findViewById<ImageButton>(R.id.btnAddClient)

        if (!StateManager.addClientButtonActivated)
            btnAddClient.visibility = View.INVISIBLE
        else btnAddClient.setOnClickListener {
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
        clientService.getClientsByUserId(preferences.getToken(), StateManager.loggedUserId)
            .enqueue(object : Callback<List<Client>> {
                override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                    if (response.isSuccessful) {
                        response.body()!!.forEach {
                            if(!clients.contains(it))
                                clients.add(it)
                        }
                        binding.rvClients.layoutManager = LinearLayoutManager(this@ClientsActivity)
                        binding.rvClients.adapter = ClientAdapter(clients, true, this@ClientsActivity, this@ClientsActivity)
                    }
                    else
                        Toast.makeText(this@ClientsActivity, "Error al obtener clientes: ${response.message()}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                    Toast.makeText(this@ClientsActivity, "Error al obtener clientes: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun toPaymentPlanResult() = startActivity(Intent(this, PaymentPlanActivity::class.java))

    private fun getPeriods(paymentPlan: PaymentPlan) {
        planService.getPeriods(preferences.getToken(), paymentPlan.id)
            .enqueue(object : Callback<List<Period>> {
                override fun onResponse(
                    call: Call<List<Period>>,
                    response: Response<List<Period>>
                ) {
                    if(response.isSuccessful) {
                      response.body().let {
                          if(it != null) {
                              StateManager.generatedPaymentPlan = paymentPlan.toSavePaymentPlan()
                              StateManager.periods = it.map { item -> item.toSavePeriod() }
                              StateManager.paymentFromBack = true
                              StateManager.paymentFromBackId = paymentPlan.id
                              toPaymentPlanResult()
                          }
                      }
                    }
                }

                override fun onFailure(call: Call<List<Period>>, t: Throwable) = showShortToast("A ocurrido un error: ${t.message}")

            })
    }

    private fun getPlan() {
        clientService = SharedMethods.retrofitBuilder().create(ClientService::class.java)
        clientService.getPlan(preferences.getToken(), StateManager.selectedClient.id)
            .enqueue(object : Callback<PaymentPlan> {
                override fun onResponse(call: Call<PaymentPlan>, response: Response<PaymentPlan>) {
                    if(response.isSuccessful) {
                        response.body().let { paymentPlan ->
                            if(StateManager.frenchButtonActivated)
                                showShortToast("El cliente ya tiene un plan de pagos")
                            else if(paymentPlan != null)
                                    getPeriods(paymentPlan)
                            else showShortToast("El cliente no tiene un plan de pagos 2")

                        }
                    }
                    else if(StateManager.frenchButtonActivated) toPaymentForm()
                    else showShortToast("El cliente no tiene un plan de pagos")
                }

                override fun onFailure(call: Call<PaymentPlan>, t: Throwable) = showShortToast("A ocurrido un error: ${t.message}")

            })
    }

    private fun toPaymentForm() = startActivity(Intent(this, PaymentFormActivity::class.java))

    override fun onItemClicked(value: Client) {
        StateManager.selectedClient = value
        getPlan()
    }

    override fun onClientDeleted(client: Client, position: Int) {
        clientService.delete(preferences.getToken(), client.id)
            .enqueue(object : Callback<Client> {
                override fun onResponse(call: Call<Client>, response: Response<Client>) {
                    if(response.isSuccessful) {
                        clients.remove(client)
                        binding.rvClients.adapter?.notifyItemRemoved(position)
                        showShortToast("Cliente eliminado exitosamente")
                    } else showShortToast("Algo salio mal, cliente no borrado")
                }

                override fun onFailure(call: Call<Client>, t: Throwable)= showShortToast("Algo salio mal: ${t.message}")

            })
    }
}