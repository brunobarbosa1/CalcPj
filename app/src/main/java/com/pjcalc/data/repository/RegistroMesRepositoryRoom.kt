package com.pjcalc.data.repository

import com.pjcalc.data.local.REGIME_ALIQUOTAS
import com.pjcalc.data.local.REGIME_MEI
import com.pjcalc.data.local.RegistroMesDao
import com.pjcalc.data.local.RegistroMesEntity
import com.pjcalc.domain.model.RegimeTributario
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
    regime = when (regime) {
        REGIME_MEI -> RegimeTributario.Mei(das = das ?: 0.0)
        REGIME_ALIQUOTAS -> RegimeTributario.Aliquotas(
            inss = aliqINSS ?: 0.0,
            iss = aliqISS ?: 0.0,
            irrf = aliqIRRF ?: 0.0
        )

        else -> error("Regime desconhecido no banco: $regime")
    },
    criadoEm = criadoEm
)

private fun RegistroMes.paraEntidade(): RegistroMesEntity {
    val base = RegistroMesEntity(
        id = id,
        ano = ano,
        mes = mes,
        horas = horas,
        valorHora = valorHora,
        regime = "",
        aliqINSS = null,
        aliqISS = null,
        aliqIRRF = null,
        das = null,
        criadoEm = criadoEm
    )
    return when (val r = regime) {
        is RegimeTributario.Aliquotas -> base.copy(
            regime = REGIME_ALIQUOTAS,
            aliqINSS = r.inss,
            aliqISS = r.iss,
            aliqIRRF = r.irrf
        )

        is RegimeTributario.Mei -> base.copy(regime = REGIME_MEI, das = r.das)
    }
}
