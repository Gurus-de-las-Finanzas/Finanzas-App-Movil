package com.example.finanzas.payments.models

data class SavePeriodResource(
    var numberPeriod: Int,
    var initialBalance: Double,
    var interest: Double,
    var amortization: Double,
    var fee: Double,
    var finalBalance: Double
)
