package com.pjcalc.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pjcalc.domain.model.Preferencias
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.TipoRegime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val TIPO_REGIME = stringPreferencesKey("tipo_regime")
private val ALIQ_INSS = doublePreferencesKey("aliq_inss")
private val ALIQ_ISS = doublePreferencesKey("aliq_iss")
private val ALIQ_IRRF = doublePreferencesKey("aliq_irrf")
private val DAS = doublePreferencesKey("das")
private val HORAS_PADRAO = doublePreferencesKey("horas_padrao")
private val VALOR_HORA_PADRAO = doublePreferencesKey("valor_hora_padrao")

class PreferenciasDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenciasRepository {
    override val preferencias: Flow<Preferencias> = dataStore.data.map { guardado ->
        val padrao = Preferencias()
        Preferencias(
            tipoRegime = guardado[TIPO_REGIME]?.let { TipoRegime.valueOf(it) } ?: padrao.tipoRegime,
            aliquotas = RegimeTributario.Aliquotas(
                inss = guardado[ALIQ_INSS] ?: padrao.aliquotas.inss,
                iss = guardado[ALIQ_ISS] ?: padrao.aliquotas.iss,
                irrf = guardado[ALIQ_IRRF] ?: padrao.aliquotas.irrf
            ),
            mei = RegimeTributario.Mei(das = guardado[DAS] ?: padrao.mei.das),
            horasPadrao = guardado[HORAS_PADRAO] ?: padrao.horasPadrao,
            valorHoraPadrao = guardado[VALOR_HORA_PADRAO] ?: padrao.valorHoraPadrao
        )
    }

    override suspend fun definirTipoRegime(tipo: TipoRegime) {
        dataStore.edit { it[TIPO_REGIME] = tipo.name }
    }

    override suspend fun definirAliquotas(aliquotas: RegimeTributario.Aliquotas) {
        dataStore.edit {
            it[ALIQ_INSS] = aliquotas.inss
            it[ALIQ_ISS] = aliquotas.iss
            it[ALIQ_IRRF] = aliquotas.irrf
        }
    }

    override suspend fun definirMei(mei: RegimeTributario.Mei) {
        dataStore.edit { it[DAS] = mei.das }
    }

    override suspend fun definirHorasPadrao(horas: Double) {
        dataStore.edit { it[HORAS_PADRAO] = horas }
    }

    override suspend fun definirValorHoraPadrao(valorHora: Double) {
        dataStore.edit { it[VALOR_HORA_PADRAO] = valorHora }
    }
}
