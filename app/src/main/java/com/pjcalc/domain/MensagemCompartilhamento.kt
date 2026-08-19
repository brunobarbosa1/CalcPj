package com.pjcalc.domain

import com.pjcalc.domain.model.ResultadoCalculo

fun mensagemCompartilhamento(
    ano: Int,
    mes: Int,
    horas: Double,
    valorHora: Double,
    aliqINSS: Double,
    aliqISS: Double,
    aliqIRRF: Double,
    resultado: ResultadoCalculo
): String = buildString {
    appendLine("Calculadora PJ · ${nomeDoMes(mes)} $ano")
    appendLine("${formatarHoras(horas)}h × ${formatarMoeda(valorHora)}/h")
    appendLine()
    appendLine("Bruto: ${formatarMoeda(resultado.bruto)}")
    appendLine("INSS ${formatarPercentual(aliqINSS)}: -${formatarMoeda(resultado.inss)}")
    appendLine("ISS ${formatarPercentual(aliqISS)}: -${formatarMoeda(resultado.iss)}")
    appendLine("IRRF ${formatarPercentual(aliqIRRF)}: -${formatarMoeda(resultado.irrf)}")
    appendLine()
    append("Líquido a receber: ${formatarMoeda(resultado.liquido)}")
}
