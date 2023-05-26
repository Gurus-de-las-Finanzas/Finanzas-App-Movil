package com.example.finanzas.clients.network

import com.example.finanzas.clients.models.Client
import com.example.finanzas.clients.models.SaveClientResource
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ClientService {
    @GET("api/v1/users/{id}/clients")
    fun getClientsByUserId(@Header("Authorization") token: String, @Path("id") id: Int): Call<List<Client>>

    @POST("api/v1/clients")
    fun saveClient(@Header("Authorization") token: String, @Body clientResource: SaveClientResource): Call<Client>
}