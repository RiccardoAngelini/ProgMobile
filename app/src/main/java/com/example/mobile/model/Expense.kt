package com.example.mobile.model

import java.util.Date


data class Expense(
    val id: String = "",
    val name: String = "",
    var amount: Double = 0.0,
    var payer: String = "", //id del pagatore
    var debtors: List<String> = emptyList(), //lista id dei debitori
    var date: Date = Date()
) {
    // Costruttore senza argomenti richiesto da Firebase Firestore
    constructor() : this("", "", 0.0, "", emptyList())
}
