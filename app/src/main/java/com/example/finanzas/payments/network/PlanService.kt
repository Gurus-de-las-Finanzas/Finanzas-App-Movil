package com.example.finanzas.payments.network

import com.example.finanzas.payments.models.PaymentPlan
import com.example.finanzas.payments.models.Period
import com.example.finanzas.payments.models.SavePaymentPlanResource
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PlanService {

    @POST("api/v1/schedules")
    fun savePlan(@Header("Authorization") token: String, @Body paymentPlanResource: SavePaymentPlanResource): Call<PaymentPlan>

    @DELETE("api/v1/schedules/{id}")
    fun deletePlan(@Header("Authorization") token: String, @Path("id") id: Int): Call<PaymentPlan>

    @GET("api/v1/schedules/{id}/periods")
    fun getPeriods(@Header("Authorization") token: String, @Path("id") id: Int): Call<List<Period>>
}