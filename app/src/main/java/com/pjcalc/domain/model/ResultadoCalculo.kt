package com.pjcalc.domain.model

data class ResultadoCalculo(
    val bruto: Double,
    val descontos: List<Desconto>,
    val liquido: Double
)
