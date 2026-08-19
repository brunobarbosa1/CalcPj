package com.pjcalc.data.repository

import com.pjcalc.domain.model.RegistroMes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class RegistroMesRepositoryFake : RegistroMesRepository {
    private val registros = MutableStateFlow<List<RegistroMes>>(emptyList())
    private var proximoId = 1L

    override fun observarTodos(): Flow<List<RegistroMes>> = registros.map { lista ->
        lista.sortedWith(
            compareByDescending<RegistroMes> { it.ano }
                .thenByDescending { it.mes }
                .thenByDescending { it.criadoEm }
        )
    }

    override suspend fun porId(id: Long): RegistroMes? = registros.value.firstOrNull { it.id == id }

    override suspend fun salvar(registro: RegistroMes): Long {
        val id = if (registro.id != 0L) registro.id else proximoId++
        registros.update { lista ->
            lista.filterNot { it.ano == registro.ano && it.mes == registro.mes } +
                registro.copy(id = id)
        }
        return id
    }
}
