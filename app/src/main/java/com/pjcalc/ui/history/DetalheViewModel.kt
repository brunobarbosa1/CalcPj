package com.pjcalc.ui.history

import androidx.lifecycle.SavedStateHandle
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

const val ARG_ID_REGISTRO = "id"

@HiltViewModel
class DetalheViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repositorio: RegistroMesRepository,
    calcularGanho: CalcularGanhoUseCase
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle[ARG_ID_REGISTRO])

    val state: StateFlow<RegistroCalculado?> = repositorio.observarTodos()
        .map { registros ->
            registros.firstOrNull { it.id == id }?.let { registro ->
                calcularGanho(
                    horas = registro.horas,
                    valorHora = registro.valorHora,
                    regime = registro.regime
                ).getOrNull()?.let { RegistroCalculado(registro, it) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
