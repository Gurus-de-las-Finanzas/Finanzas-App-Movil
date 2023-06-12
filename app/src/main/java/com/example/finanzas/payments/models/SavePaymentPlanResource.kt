package com.example.finanzas.payments.models

data class SavePaymentPlanResource(
    var coin: Char,
    var periodQuantity: Int,
    var rateType: String,
    var rate: Double,
    var propertyCost: Double,
    var clientId: Int,
    var periods: List<SavePeriodResource>,
    var graceMonths: Int,
    var initialFee: Double,
    var gracePeriod: Char,
    var loan: Double
)
