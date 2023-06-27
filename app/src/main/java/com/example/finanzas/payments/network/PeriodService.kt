package com.example.finanzas.payments.network

import com.example.finanzas.payments.models.Period
import com.example.finanzas.payments.models.SavePeriodResource
import com.example.finanzas.shared.models.MessageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PeriodService {
    @POST("api/v1/periods")
    fun savePeriod(@Header("Authorization") token: String, @Body periodResource: SavePeriodResource): Call<Period>

    @POST("api/v1/periods/many")
    fun saveManyPeriods(@Header("Authorization") token: String, @Body periodResources: List<SavePeriodResource>): Call<MessageResponse>
}