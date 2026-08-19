package com.pjcalc.domain.model

/**
 * Uma linha do breakdown. [detalhe] é o texto discreto ao lado do nome
 * ("11%", "valor fixo").
 */
data class Desconto(
    val nome: String,
    val detalhe: String?,
    val valor: Double
)
