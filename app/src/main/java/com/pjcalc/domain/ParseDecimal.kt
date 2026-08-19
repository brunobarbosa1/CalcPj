package com.pjcalc.domain

private val NUMERO = Regex("""^\d+([.,]\d*)?$""")

fun parseDecimal(texto: String): Double? {
    val limpo = texto.trim()
    if (!NUMERO.matches(limpo)) return null
    return limpo.replace(',', '.').removeSuffix(".").toDoubleOrNull()
}
