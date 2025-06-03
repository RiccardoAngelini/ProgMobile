package com.example.mobile.model

data class User @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    var userExpense: List<String> = emptyList(), //lista id spese associate all'utente
    var userPayment: List<String> = emptyList(),//lista id pagamenti associate all'utente

)
