package com.example.finanzas.payments.models

class PaymentPlanContainer {
    lateinit var newPlan: SavePaymentPlanResource
    lateinit var oldPlan: PaymentPlan
    var isNew: Boolean = true
    constructor(newPlan: SavePaymentPlanResource) {
        this.newPlan = newPlan
        isNew = true
    }
    constructor(oldPlan: PaymentPlan) {
        this.oldPlan = oldPlan
        isNew = false
    }
}