package com.pjcalc.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjcalc.data.repository.RegistroMesRepository
import com.pjcalc.domain.model.RegistroMes
import com.pjcalc.domain.model.ResultadoCalculo
import com.pjcalc.domain.parseDecimal
import com.pjcalc.domain.usecase.CalcularGanhoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val horas: String = "160",
    val valorHora: String = "",
    val erroHoras: String? = null,
    val erroValorHora: String? = null,
    val resultado: ResultadoCalculo? = null,
    val salvo: Boolean = false,
    val horasCalculadas: Double = 0.0,
    val valorHoraCalculado: Double = 0.0,
    val ano: Int = LocalDate.now().year,
    val mes: Int = LocalDate.now().monthValue,
    val horasPadrao: Double = 160.0,
    val aliqINSS: Double = 11.0,
    val aliqISS: Double = 5.0,
    val aliqIRRF: Double = 1.5
) {
    val podeCalcular: Boolean
        get() = parseDecimal(horas) != null && parseDecimal(valorHora) != null
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val calcularGanho: CalcularGanhoUseCase,
    private val repositorio: RegistroMesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun aoMudarHoras(texto: String) {
        _state.update {
            it.copy(horas = texto, erroHoras = erroDe(texto), resultado = null, salvo = false)
        }
    }

    fun aoMudarValorHora(texto: String) {
        _state.update {
            it.copy(valorHora = texto, erroValorHora = erroDe(texto), resultado = null, salvo = false)
        }
    }

    fun calcular() {
        val atual = _state.value
        val horas = parseDecimal(atual.horas) ?: return
        val valorHora = parseDecimal(atual.valorHora) ?: return

        calcularGanho(
            horas = horas,
            valorHora = valorHora,
            aliqINSS = atual.aliqINSS,
            aliqISS = atual.aliqISS,
            aliqIRRF = atual.aliqIRRF
        ).onSuccess { resultado ->
            _state.update {
                it.copy(
                    resultado = resultado,
                    horasCalculadas = horas,
                    valorHoraCalculado = valorHora,
                    salvo = false
                )
            }
        }
    }

    fun salvarMes() {
        val atual = _state.value
        if (atual.resultado == null) return

        viewModelScope.launch {
            repositorio.salvar(
                RegistroMes(
                    ano = atual.ano,
                    mes = atual.mes,
                    horas = atual.horasCalculadas,
                    valorHora = atual.valorHoraCalculado,
                    aliqINSS = atual.aliqINSS,
                    aliqISS = atual.aliqISS,
                    aliqIRRF = atual.aliqIRRF,
                    criadoEm = System.currentTimeMillis()
                )
            )
            _state.update { it.copy(salvo = true) }
        }
    }

    private fun erroDe(texto: String): String? = when {
        texto.isBlank() -> null
        parseDecimal(texto) == null -> "Valor inválido"
        else -> null
    }
}
