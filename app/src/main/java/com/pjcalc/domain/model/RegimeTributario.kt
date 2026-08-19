package com.pjcalc.domain.model

/**
 * Como os impostos incidem sobre o faturamento do mês.
 *
 * Ao adicionar um regime novo aqui, o `when` do CalcularGanhoUseCase deixa de
 * compilar até que ele seja tratado — que é exatamente o que queremos.
 */
sealed interface RegimeTributario {

    val rotulo: String

    /** Percentuais que incidem sobre o bruto. */
    data class Aliquotas(
        val inss: Double,
        val iss: Double,
        val irrf: Double
    ) : RegimeTributario {
        override val rotulo get() = "Alíquotas"
    }

    /** DAS é um valor fixo mensal: não acompanha as horas trabalhadas. */
    data class Mei(val das: Double) : RegimeTributario {
        override val rotulo get() = "MEI"
    }
}
