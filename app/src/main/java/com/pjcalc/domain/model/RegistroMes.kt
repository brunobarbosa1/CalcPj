package com.pjcalc.domain.model

data class RegistroMes(
    val id: Long = 0,
    val ano: Int,
    val mes: Int,
    val horas: Double,
    val valorHora: Double,
    val regime: RegimeTributario,
    val criadoEm: Long
)
