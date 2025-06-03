package com.example.mobile.model

data class Group(
    var id: String = "", // Inizializzati con valori predefiniti
    var name: String = "",
    var type: String = "",
    var members: List<String> = emptyList(), //lista id dei membri
    var groupExpense: List<String> = emptyList(), //lista degli id dei pagamenti e delle spese
    var sale:Map<String,Double> = emptyMap(),
    var idAmministratore: String = "",
)


