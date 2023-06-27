package com.example.finanzas.security.controller.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.finanzas.R
import com.example.finanzas.home.controller.activities.HomeActivity
import com.example.finanzas.security.models.AuthenticateRequest
import com.example.finanzas.security.models.AuthenticateResponse
import com.example.finanzas.security.models.LoginCredentials
import com.example.finanzas.security.models.User
import com.example.finanzas.security.network.UserService
import com.example.finanzas.shared.AppDatabase
import com.example.finanzas.shared.AppPreferences.Companion.preferences
import com.example.finanzas.shared.SharedMethods
import com.example.finanzas.shared.StateManager
import retrofit2.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            register()
        }

    }

    private fun register() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun signIn(view: View) {
        val etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = findViewById<EditText>(R.id.etLoginPassword)
        val retrofit = SharedMethods.retrofitBuilder()

        val userService: UserService = retrofit.create(UserService::class.java)

        val request = userService.signIn(AuthenticateRequest(etLoginEmail.text.toString(), etLoginPassword.text.toString()))

        request.enqueue(object : Callback<AuthenticateResponse> {
            override fun onResponse(
                call: Call<AuthenticateResponse>,
                response: Response<AuthenticateResponse>
            ) {
                if (response.isSuccessful) {
                    AppDatabase.getInstance(this@LoginActivity).getLoginCredentialsDao().insertCredentials(
                        LoginCredentials(null, etLoginEmail.text.toString(), etLoginPassword.text.toString())
                    )
                    goToHome(response.body()!!)
                }
                else
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<AuthenticateResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error al iniciar sesión.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun goToHome(authenticateResponse: AuthenticateResponse) {
        StateManager.authToken = "Bearer ${authenticateResponse.token}"
        preferences.saveToken("Bearer ${authenticateResponse.token}")
        StateManager.loggedUser = User(
            authenticateResponse.id,
            authenticateResponse.name,
            authenticateResponse.lastName,
            authenticateResponse.age,
            authenticateResponse.image,
            authenticateResponse.email,
        )
        StateManager.loggedUserId = authenticateResponse.id
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}