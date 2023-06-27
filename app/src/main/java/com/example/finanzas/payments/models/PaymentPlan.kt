package com.example.finanzas.payments.models

import com.example.finanzas.clients.models.Client

data class PaymentPlan(
    var id: Int,
    var coin: Char,
    var periods: Int,
    var typeRate: Char,
    var interestRate: Double,
    var propertyCost: Double,
    var graceMonths: Int?,
    var initialFeePercent: Double,
    var gracePeriod: Char?,
    var loan: Double,
    var goodPayerBonus: Double?,
    var miViviendaBonus: Double?,
    var modality: String,
    var name: String,
    var date: String,
    var typePeriod: String,
    var client: Client,
    var clientId: Int
)
