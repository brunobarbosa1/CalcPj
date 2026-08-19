package com.pjcalc.domain

private val NUMERO = Regex("""^\d+([.,]\d*)?$""")

/**
 * Converte o texto digitado pelo usuário em um número.
 * Aceita vírgula ou ponto como separador decimal. Devolve null quando o texto
 * não é um número não-negativo válido.
 */
fun parseDecimal(texto: String): Double? {
    val limpo = texto.trim()
    if (!NUMERO.matches(limpo)) return null
    return limpo.replace(',', '.').removeSuffix(".").toDoubleOrNull()
}
