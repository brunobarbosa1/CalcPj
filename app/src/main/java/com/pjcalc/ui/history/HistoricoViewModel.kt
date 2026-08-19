package com.pjcalc.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjcalc.data.repository.RegistroMesRepository
import com.pjcalc.domain.model.RegistroCalculado
import com.pjcalc.domain.usecase.CalcularGanhoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val MESES_NO_GRAFICO = 6

data class HistoricoUiState(
    val registros: List<RegistroCalculado> = emptyList(),
    val carregando: Boolean = true
) {
    /** Do mais antigo para o mais novo, que é como o gráfico é lido. */
    val ultimosSeis: List<RegistroCalculado>
        get() = registros.take(MESES_NO_GRAFICO).reversed()
}

@HiltViewModel
class HistoricoViewModel @Inject constructor(
    repositorio: RegistroMesRepository,
    calcularGanho: CalcularGanhoUseCase
) : ViewModel() {

    val state: StateFlow<HistoricoUiState> = repositorio.observarTodos()
        .map { registros ->
            HistoricoUiState(
                registros = registros.mapNotNull { registro ->
                    calcularGanho(
                        horas = registro.horas,
                        valorHora = registro.valorHora,
                        aliqINSS = registro.aliqINSS,
                        aliqISS = registro.aliqISS,
                        aliqIRRF = registro.aliqIRRF
                    ).getOrNull()?.let { RegistroCalculado(registro, it) }
                },
                carregando = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoricoUiState()
        )
}
