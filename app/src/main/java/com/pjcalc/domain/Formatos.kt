package com.pjcalc.domain

import java.text.NumberFormat
import java.time.Month
import java.time.format.TextStyle

private fun numeroEnxuto(valor: Double, maxCasas: Int): String =
    NumberFormat.getNumberInstance(LOCALE_BR).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = maxCasas
        isGroupingUsed = false
    }.format(valor)

fun formatarPercentual(valor: Double): String = numeroEnxuto(valor, 2) + "%"

fun formatarHoras(valor: Double): String = numeroEnxuto(valor, 2)

fun nomeDoMes(mes: Int): String =
    Month.of(mes).getDisplayName(TextStyle.FULL, LOCALE_BR)
        .replaceFirstChar { it.uppercase() }

fun abreviacaoMes(mes: Int): String = nomeDoMes(mes).take(3).uppercase()

fun dividirCentavos(texto: String): Pair<String, String> {
    val separador = texto.indexOfLast { it == ',' || it == '.' }
    if (separador < 0) return texto to ""
    return texto.substring(0, separador) to texto.substring(separador)
}
