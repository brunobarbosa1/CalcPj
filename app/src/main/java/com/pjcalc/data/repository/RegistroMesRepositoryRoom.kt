package com.pjcalc.data.repository

import com.pjcalc.data.local.RegistroMesDao
import com.pjcalc.data.local.RegistroMesEntity
import com.pjcalc.domain.model.RegistroMes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegistroMesRepositoryRoom @Inject constructor(
    private val dao: RegistroMesDao
) : RegistroMesRepository {

    override fun observarTodos(): Flow<List<RegistroMes>> =
        dao.observarTodos().map { lista -> lista.map { it.paraDominio() } }

    override suspend fun porId(id: Long): RegistroMes? = dao.porId(id)?.paraDominio()

    override suspend fun salvar(registro: RegistroMes): Long = dao.salvar(registro.paraEntidade())
}

private fun RegistroMesEntity.paraDominio() = RegistroMes(
    id = id,
    ano = ano,
    mes = mes,
    horas = horas,
    valorHora = valorHora,
    aliqINSS = aliqINSS,
    aliqISS = aliqISS,
    aliqIRRF = aliqIRRF,
    criadoEm = criadoEm
)

private fun RegistroMes.paraEntidade() = RegistroMesEntity(
    id = id,
    ano = ano,
    mes = mes,
    horas = horas,
    valorHora = valorHora,
    aliqINSS = aliqINSS,
    aliqISS = aliqISS,
    aliqIRRF = aliqIRRF,
    criadoEm = criadoEm
)
