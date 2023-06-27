package com.example.finanzas.payments.models

data class Period(
    var id: Int,
    var schedule: PaymentPlan,
    var numberPeriod: Int,
    var initialBalance: Double,
    var interest: Double,
    var lienInsurance: Double,
    var propertyInsurance: Double,
    var amortization: Double,
    var fee: Double,
    var finalBalance: Double
)