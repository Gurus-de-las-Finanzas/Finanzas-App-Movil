package com.example.finanzas.shared

import com.example.finanzas.clients.models.Client
import com.example.finanzas.payments.models.SavePaymentPlanResource
import com.example.finanzas.security.models.User

object StateManager {
    lateinit var authToken: String
    var loggedUserId: Int = -1
    lateinit var loggedUser: User
    var password: String = ""
    var frenchButtonActivated: Boolean = false
    var addClientButtonActivated: Boolean = false
    lateinit var selectedClient: Client
    lateinit var generatedPaymentPlan: SavePaymentPlanResource
    var netFlowQuantity: Int = -1
}