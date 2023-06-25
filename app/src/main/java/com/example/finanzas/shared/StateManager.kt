package com.example.finanzas.shared

import com.example.finanzas.clients.models.Client
import com.example.finanzas.payments.models.SavePaymentPlanResource
import com.example.finanzas.security.models.User
import com.example.finanzas.vantir.models.VanData

object StateManager {
    var authToken: String? = null
    var loggedUserId: Int = -1
    lateinit var loggedUser: User
    var password: String = ""
    var frenchButtonActivated: Boolean = false
    var addClientButtonActivated: Boolean = false
    lateinit var selectedClient: Client
    lateinit var generatedPaymentPlan: SavePaymentPlanResource
    //var netFlowQuantity: Int = -1
    lateinit var vanData: VanData
    var vanResult: Double = -1.0
    var tirResult: Double = -1.0
}