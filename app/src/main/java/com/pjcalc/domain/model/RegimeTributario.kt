package com.pjcalc.domain.model

sealed interface RegimeTributario {
    val rotulo: String

    data class Aliquotas(
        val inss: Double,
        val iss: Double,
        val irrf: Double
    ) : RegimeTributario {
        override val rotulo get() = "Alíquotas"
    }

    data class Mei(val das: Double) : RegimeTributario {
        override val rotulo get() = "MEI"
    }
}
