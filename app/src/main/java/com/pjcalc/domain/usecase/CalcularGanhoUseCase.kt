package com.pjcalc.domain.usecase

import com.pjcalc.domain.formatarPercentual
import com.pjcalc.domain.model.Desconto
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.ResultadoCalculo
import javax.inject.Inject

class CalcularGanhoUseCase @Inject constructor() {

    operator fun invoke(
        horas: Double,
        valorHora: Double,
        regime: RegimeTributario
    ): Result<ResultadoCalculo> {
        val valores = listOf(horas, valorHora) + when (regime) {
            is RegimeTributario.Aliquotas -> listOf(regime.inss, regime.iss, regime.irrf)
            is RegimeTributario.Mei -> listOf(regime.das)
        }
        if (valores.any { it < 0 || it.isNaN() }) {
            return Result.failure(IllegalArgumentException("Valores não podem ser negativos"))
        }

        val bruto = horas * valorHora
        val descontos = when (regime) {
            is RegimeTributario.Aliquotas -> listOf(
                Desconto("INSS", formatarPercentual(regime.inss), bruto * regime.inss / 100),
                Desconto("ISS", formatarPercentual(regime.iss), bruto * regime.iss / 100),
                Desconto("IRRF", formatarPercentual(regime.irrf), bruto * regime.irrf / 100)
            )

            is RegimeTributario.Mei -> listOf(
                Desconto("DAS", "valor fixo", regime.das)
            )
        }

        return Result.success(
            ResultadoCalculo(
                bruto = bruto,
                descontos = descontos,
                liquido = bruto - descontos.sumOf { it.valor }
            )
        )
    }
}
