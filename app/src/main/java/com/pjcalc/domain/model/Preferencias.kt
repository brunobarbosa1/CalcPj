package com.pjcalc.domain.model

enum class TipoRegime { ALIQUOTAS, MEI }

/**
 * As configurações dos dois regimes ficam guardadas ao mesmo tempo: trocar de
 * regime não apaga o que estava configurado no outro.
 */
data class Preferencias(
    val tipoRegime: TipoRegime = TipoRegime.ALIQUOTAS,
    val aliquotas: RegimeTributario.Aliquotas = RegimeTributario.Aliquotas(
        inss = 11.0,
        iss = 5.0,
        irrf = 1.5
    ),
    val mei: RegimeTributario.Mei = RegimeTributario.Mei(das = 0.0),
    val horasPadrao: Double = 160.0,
    val valorHoraPadrao: Double = 0.0
) {
    val regime: RegimeTributario
        get() = when (tipoRegime) {
            TipoRegime.ALIQUOTAS -> aliquotas
            TipoRegime.MEI -> mei
        }
}
