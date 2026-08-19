package com.pjcalc.data.prefs

import com.pjcalc.domain.model.Preferencias
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.TipoRegime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PreferenciasRepositoryFake(inicial: Preferencias = Preferencias()) : PreferenciasRepository {
    private val estado = MutableStateFlow(inicial)

    override val preferencias: Flow<Preferencias> = estado

    override suspend fun definirTipoRegime(tipo: TipoRegime) {
        estado.update { it.copy(tipoRegime = tipo) }
    }

    override suspend fun definirAliquotas(aliquotas: RegimeTributario.Aliquotas) {
        estado.update { it.copy(aliquotas = aliquotas) }
    }

    override suspend fun definirMei(mei: RegimeTributario.Mei) {
        estado.update { it.copy(mei = mei) }
    }

    override suspend fun definirHorasPadrao(horas: Double) {
        estado.update { it.copy(horasPadrao = horas) }
    }

    override suspend fun definirValorHoraPadrao(valorHora: Double) {
        estado.update { it.copy(valorHoraPadrao = valorHora) }
    }
}
