package com.example.finanzas.payments.models

data class PaymentPlan(
    var id: Int,
    var coin: Char,
    var periodQuantity: Int,
    var propertyCost: Double,
    var clientId: Int,
    var periods: List<SavePeriodResource>
    )
