package com.pjcalc.ui.history

import com.pjcalc.data.repository.RegistroMesRepositoryFake
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.RegistroMes
import com.pjcalc.domain.usecase.CalcularGanhoUseCase
import com.pjcalc.util.DispatcherPrincipalRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HistoricoViewModelTest {
    @get:Rule
    val dispatcherRule = DispatcherPrincipalRule()

    private val repositorio = RegistroMesRepositoryFake()

    private fun viewModel() = HistoricoViewModel(repositorio, CalcularGanhoUseCase())

    private suspend fun estadoCarregado(vm: HistoricoViewModel) =
        vm.state.first { !it.carregando }

    @Test
    fun `lista vem do mes mais recente para o mais antigo`() = runTest {
        repositorio.salvar(registro(ano = 2026, mes = 6))
        repositorio.salvar(registro(ano = 2026, mes = 8))
        repositorio.salvar(registro(ano = 2026, mes = 7))

        val estado = estadoCarregado(viewModel())

        assertEquals(listOf(8, 7, 6), estado.registros.map { it.registro.mes })
    }

    @Test
    fun `cada item traz o liquido ja calculado`() = runTest {
        repositorio.salvar(registro(ano = 2026, mes = 8, horas = 168.0, valorHora = 120.0))

        val estado = estadoCarregado(viewModel())

        assertEquals(20160.00, estado.registros.single().resultado.bruto, 0.001)
        assertEquals(16632.00, estado.registros.single().resultado.liquido, 0.001)
    }

    @Test
    fun `o grafico usa no maximo seis meses, do mais antigo para o mais novo`() = runTest {
        (1..9).forEach { mes -> repositorio.salvar(registro(ano = 2026, mes = mes)) }

        val estado = estadoCarregado(viewModel())

        assertEquals(listOf(4, 5, 6, 7, 8, 9), estado.ultimosSeis.map { it.registro.mes })
    }

    @Test
    fun `com menos de seis meses o grafico mostra o que existe`() = runTest {
        repositorio.salvar(registro(ano = 2026, mes = 7))
        repositorio.salvar(registro(ano = 2026, mes = 8))

        val estado = estadoCarregado(viewModel())

        assertEquals(listOf(7, 8), estado.ultimosSeis.map { it.registro.mes })
    }

    @Test
    fun `meses de regimes diferentes convivem com seus proprios descontos`() = runTest {
        repositorio.salvar(registro(ano = 2026, mes = 7, horas = 160.0, valorHora = 120.0))
        repositorio.salvar(
            registro(
                ano = 2026, mes = 8, horas = 160.0, valorHora = 120.0,
                regime = RegimeTributario.Mei(80.90)
            )
        )

        val estado = estadoCarregado(viewModel())
        val agosto = estado.registros.first { it.registro.mes == 8 }
        val julho = estado.registros.first { it.registro.mes == 7 }

        assertEquals(listOf("DAS"), agosto.resultado.descontos.map { it.nome })
        assertEquals(19119.10, agosto.resultado.liquido, 0.001)
        assertEquals(listOf("INSS", "ISS", "IRRF"), julho.resultado.descontos.map { it.nome })
        assertEquals(15840.00, julho.resultado.liquido, 0.001)
    }

    @Test
    fun `historico vazio nao quebra`() = runTest {
        val estado = estadoCarregado(viewModel())

        assertTrue(estado.registros.isEmpty())
        assertTrue(estado.ultimosSeis.isEmpty())
    }

    private fun registro(
        ano: Int,
        mes: Int,
        horas: Double = 160.0,
        valorHora: Double = 100.0,
        regime: RegimeTributario = RegimeTributario.Aliquotas(inss = 11.0, iss = 5.0, irrf = 1.5)
    ) = RegistroMes(
        ano = ano,
        mes = mes,
        horas = horas,
        valorHora = valorHora,
        regime = regime,
        criadoEm = 0L
    )
}
