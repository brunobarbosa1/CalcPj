package com.pjcalc.data.prefs

import com.pjcalc.domain.model.Preferencias
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.TipoRegime
import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {
    val preferencias: Flow<Preferencias>
    suspend fun definirTipoRegime(tipo: TipoRegime)
    suspend fun definirAliquotas(aliquotas: RegimeTributario.Aliquotas)
    suspend fun definirMei(mei: RegimeTributario.Mei)
    suspend fun definirHorasPadrao(horas: Double)
    suspend fun definirValorHoraPadrao(valorHora: Double)
}
