package com.example.mobile.model

import java.util.Date

data class Payment(
    val id: String = "",
    val name: String = "",
    var amount: Double = 0.0,
    var expense: String = "", //id della spesa associata al pagamento
    var date: Date = Date()
) {

    constructor() : this("", "", 0.0, "")
}
