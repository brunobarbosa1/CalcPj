package com.pjcalc.domain

import java.text.NumberFormat
import java.util.Locale

internal val LOCALE_BR: Locale = Locale.forLanguageTag("pt-BR")

fun formatarMoeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor)

fun formatarValor(valor: Double): String =
    NumberFormat.getNumberInstance(LOCALE_BR).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(valor)

fun formatarMoedaCompacta(valor: Double): String =
    NumberFormat.getCurrencyInstance(LOCALE_BR).apply {
        maximumFractionDigits = 0
    }.format(valor)
