package com.pjcalc.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjcalc.data.prefs.PreferenciasRepository
import com.pjcalc.domain.formatarHoras
import com.pjcalc.domain.model.Preferencias
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.TipoRegime
import com.pjcalc.domain.parseDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AjustesUiState(
    val tipoRegime: TipoRegime = TipoRegime.ALIQUOTAS,
    val horasPadrao: String = "",
    val valorHoraPadrao: String = "",
    val inss: String = "",
    val iss: String = "",
    val irrf: String = "",
    val das: String = "",
    val erroHorasPadrao: String? = null,
    val erroValorHoraPadrao: String? = null,
    val erroInss: String? = null,
    val erroIss: String? = null,
    val erroIrrf: String? = null,
    val erroDas: String? = null
)

private const val INVALIDO = "Valor inválido"

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val preferencias: PreferenciasRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AjustesUiState())
    val state: StateFlow<AjustesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            preferencias.preferencias.collect { prefs -> semear(prefs) }
        }
    }

    private fun semear(prefs: Preferencias) {
        _state.update { atual ->
            atual.copy(
                tipoRegime = prefs.tipoRegime,
                horasPadrao = atual.horasPadrao.ifEmpty { textoOuVazio(prefs.horasPadrao) },
                valorHoraPadrao = atual.valorHoraPadrao.ifEmpty {
                    textoOuVazio(prefs.valorHoraPadrao)
                },
                inss = atual.inss.ifEmpty { formatarHoras(prefs.aliquotas.inss) },
                iss = atual.iss.ifEmpty { formatarHoras(prefs.aliquotas.iss) },
                irrf = atual.irrf.ifEmpty { formatarHoras(prefs.aliquotas.irrf) },
                das = atual.das.ifEmpty { textoOuVazio(prefs.mei.das) }
            )
        }
    }

    private fun textoOuVazio(valor: Double) = if (valor > 0) formatarHoras(valor) else ""

    fun aoTrocarRegime(tipo: TipoRegime) {
        viewModelScope.launch { preferencias.definirTipoRegime(tipo) }
    }

    fun aoMudarHorasPadrao(texto: String) {
        _state.update { it.copy(horasPadrao = texto, erroHorasPadrao = erroDe(texto)) }
        parseDecimal(texto)?.let { horas ->
            viewModelScope.launch { preferencias.definirHorasPadrao(horas) }
        }
    }

    fun aoMudarValorHoraPadrao(texto: String) {
        _state.update { it.copy(valorHoraPadrao = texto, erroValorHoraPadrao = erroDe(texto)) }
        parseDecimal(texto)?.let { valor ->
            viewModelScope.launch { preferencias.definirValorHoraPadrao(valor) }
        }
    }

    fun aoMudarInss(texto: String) {
        _state.update { it.copy(inss = texto, erroInss = erroDe(texto)) }
        gravarAliquotas()
    }

    fun aoMudarIss(texto: String) {
        _state.update { it.copy(iss = texto, erroIss = erroDe(texto)) }
        gravarAliquotas()
    }

    fun aoMudarIrrf(texto: String) {
        _state.update { it.copy(irrf = texto, erroIrrf = erroDe(texto)) }
        gravarAliquotas()
    }

    fun aoMudarDas(texto: String) {
        _state.update { it.copy(das = texto, erroDas = erroDe(texto)) }
        val das = parseDecimal(texto) ?: return
        viewModelScope.launch { preferencias.definirMei(RegimeTributario.Mei(das)) }
    }

    private fun gravarAliquotas() {
        val atual = _state.value
        viewModelScope.launch {
            val guardadas = preferencias.preferencias.first().aliquotas
            val inss = parseDecimal(atual.inss) ?: guardadas.inss
            val iss = parseDecimal(atual.iss) ?: guardadas.iss
            val irrf = parseDecimal(atual.irrf) ?: guardadas.irrf
            preferencias.definirAliquotas(
                RegimeTributario.Aliquotas(inss = inss, iss = iss, irrf = irrf)
            )
        }
    }

    private fun erroDe(texto: String): String? = when {
        texto.isBlank() -> null
        parseDecimal(texto) == null -> INVALIDO
        else -> null
    }
}
