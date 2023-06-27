package com.example.finanzas.security.controller.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.finanzas.R
import com.example.finanzas.security.models.RegisterRequest
import com.example.finanzas.security.models.RegisterUpdateResponse
import com.example.finanzas.security.network.UserService
import com.example.finanzas.shared.SharedMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnConfirmRegister = findViewById<Button>(R.id.btnConfirmRegister)
        btnConfirmRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val etRegisterEmail = findViewById<EditText>(R.id.etRegisterEmail)
        val etRegisterPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etRegisterName = findViewById<EditText>(R.id.etRegisterName)
        val etRegisterLastName = findViewById<EditText>(R.id.etRegisterLastName)
        val etRegisterAge = findViewById<EditText>(R.id.etRegisterAge)
        val etRegisterImage = findViewById<EditText>(R.id.etRegisterImage)

        val retrofit = SharedMethods.retrofitBuilder()

        val userService: UserService = retrofit.create(UserService::class.java)

        val request = userService.signUp(RegisterRequest(
            etRegisterName.text.toString(),
            etRegisterLastName.text.toString(),
            etRegisterAge.text.toString().toInt(),
            etRegisterImage.text.toString(),
            etRegisterEmail.text.toString(),
            etRegisterPassword.text.toString()
        ))

        request.enqueue(object : Callback<RegisterUpdateResponse> {
            override fun onResponse(call: Call<RegisterUpdateResponse>, response: Response<RegisterUpdateResponse>) {
                if(response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                    finish()
                }
                else
                    Toast.makeText(this@RegisterActivity, "Error al registrar usuario: ${response.message()}", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<RegisterUpdateResponse>, t: Throwable) {
                Log.d("FAILURE", t.message.toString())
                Toast.makeText(this@RegisterActivity, "Error al registrar usuario: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }
}