package com.pjcalc.domain

import com.pjcalc.domain.model.ResultadoCalculo

fun mensagemCompartilhamento(
    ano: Int,
    mes: Int,
    horas: Double,
    valorHora: Double,
    resultado: ResultadoCalculo
): String = buildString {
    appendLine("Calculadora PJ · ${nomeDoMes(mes)} $ano")
    appendLine("${formatarHoras(horas)}h × ${formatarMoeda(valorHora)}/h")
    appendLine()
    appendLine("Bruto: ${formatarMoeda(resultado.bruto)}")
    resultado.descontos.forEach { desconto ->
        val rotulo = listOfNotNull(desconto.nome, desconto.detalhe).joinToString(" ")
        appendLine("$rotulo: -${formatarMoeda(desconto.valor)}")
    }
    appendLine()
    append("Líquido a receber: ${formatarMoeda(resultado.liquido)}")
}
