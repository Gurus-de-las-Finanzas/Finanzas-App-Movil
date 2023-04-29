package com.example.finanzas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.security.controller.activities.LoginActivity
import com.example.finanzas.security.models.AuthenticateRequest
import com.example.finanzas.security.models.AuthenticateResponse
import com.example.finanzas.security.models.LoginCredentials
import com.example.finanzas.security.models.User
import com.example.finanzas.security.network.UserService
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.SharedMethods
import com.example.finanzas.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //val tvStart = findViewById<TextView>(R.id.tvStart)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        autoLogin()
        //tvStart.text = "Presione atrás para salir."
    }

    private fun autoLogin() {
        val query = AppDatabase.getInstance(this).getLoginCredentialsDao().getAll()
        if (query.isNotEmpty()) {
            val loginCredentials = query[0]
            val retrofit = SharedMethods.retrofitBuilder()
            val userService = retrofit.create(UserService::class.java)
            val request = userService.signIn(AuthenticateRequest(loginCredentials.email, loginCredentials.password))

            request.enqueue(object : Callback<AuthenticateResponse> {
                override fun onResponse(
                    call: Call<AuthenticateResponse>,
                    response: Response<AuthenticateResponse>
                ) {
                    if (response.isSuccessful)
                        goToHome(response.body()!!)
                    else {
                        AppDatabase.getInstance(this@MainActivity).getLoginCredentialsDao().cleanTable()
                        Toast.makeText(this@MainActivity, "Error al iniciar sesión de forma automatica", Toast.LENGTH_SHORT).show()
                        goToLoginActivity()
                    }
                }

                override fun onFailure(call: Call<AuthenticateResponse>, t: Throwable) {
                    AppDatabase.getInstance(this@MainActivity).getLoginCredentialsDao().cleanTable()
                    Toast.makeText(this@MainActivity, "Error al iniciar sesión de forma automatica", Toast.LENGTH_SHORT).show()
                    goToLoginActivity()
                }

            })
        }
        else goToLoginActivity()
    }

    private fun goToHome(authenticateResponse: AuthenticateResponse) {
        StateManager.authToken = "Bearer ${authenticateResponse.token}"
        StateManager.loggedUser = User(
            authenticateResponse.id,
            authenticateResponse.name,
            authenticateResponse.lastName,
            authenticateResponse.age,
            authenticateResponse.image,
            authenticateResponse.email,
        )
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}