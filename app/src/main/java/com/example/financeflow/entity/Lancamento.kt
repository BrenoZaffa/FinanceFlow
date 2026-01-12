package com.example.financeflow.entity

data class Lancamento(
    val _id: Int,
    val descricao: String,
    val tipo: Int, // 1 - Receita, 2 - Despesa
    val valor: Double,
    val data: Long,
)