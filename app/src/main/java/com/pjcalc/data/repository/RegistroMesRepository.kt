package com.pjcalc.data.repository

import com.pjcalc.domain.model.RegistroMes
import kotlinx.coroutines.flow.Flow

interface RegistroMesRepository {
    fun observarTodos(): Flow<List<RegistroMes>>
    suspend fun porId(id: Long): RegistroMes?
    suspend fun salvar(registro: RegistroMes): Long
}
