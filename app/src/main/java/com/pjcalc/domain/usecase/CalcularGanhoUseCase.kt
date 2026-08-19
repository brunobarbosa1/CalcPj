package com.pjcalc.domain.usecase

import com.pjcalc.domain.model.ResultadoCalculo
import javax.inject.Inject

class CalcularGanhoUseCase @Inject constructor() {

    operator fun invoke(
        horas: Double,
        valorHora: Double,
        aliqINSS: Double,
        aliqISS: Double,
        aliqIRRF: Double
    ): Result<ResultadoCalculo> {
        val invalido = listOf(horas, valorHora, aliqINSS, aliqISS, aliqIRRF)
            .any {
                it < 0 || it.isNaN()
            }
        if (invalido) {
            return Result.failure(IllegalArgumentException("Valores não podem ser negativos"))
        }

        val bruto = horas * valorHora
        val inss = bruto * (aliqINSS / 100)
        val iss = bruto * (aliqISS / 100)
        val irrf = bruto * (aliqIRRF / 100)

        return Result.success(
            ResultadoCalculo(
                bruto = bruto,
                inss = inss,
                iss = iss,
                irrf = irrf,
                liquido = bruto - inss - iss - irrf
            )
        )
    }
}
