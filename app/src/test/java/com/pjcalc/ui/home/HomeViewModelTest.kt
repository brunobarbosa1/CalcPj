package com.pjcalc.ui.home

import com.pjcalc.data.prefs.PreferenciasRepositoryFake
import com.pjcalc.data.repository.RegistroMesRepositoryFake
import com.pjcalc.domain.model.Preferencias
import com.pjcalc.domain.model.TipoRegime
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.usecase.CalcularGanhoUseCase
import com.pjcalc.util.DispatcherPrincipalRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val dispatcherRule = DispatcherPrincipalRule()

    private val repositorio = RegistroMesRepositoryFake()
    private val preferencias = PreferenciasRepositoryFake(
        Preferencias(mei = RegimeTributario.Mei(das = 80.90))
    )

    private fun viewModel() = HomeViewModel(CalcularGanhoUseCase(), repositorio, preferencias)

    @Test
    fun `as horas padrao das preferencias preenchem o campo`() = runTest {
        val vm = HomeViewModel(
            CalcularGanhoUseCase(),
            repositorio,
            PreferenciasRepositoryFake(Preferencias(horasPadrao = 176.0))
        )

        assertEquals("176", vm.state.value.horas)
    }

    @Test
    fun `editar as horas ganha das preferencias`() = runTest {
        val prefs = PreferenciasRepositoryFake(Preferencias(horasPadrao = 160.0))
        val vm = HomeViewModel(CalcularGanhoUseCase(), repositorio, prefs)

        vm.aoMudarHoras("100")
        prefs.definirHorasPadrao(176.0)

        assertEquals("100", vm.state.value.horas)
    }

    @Test
    fun `comeca com as horas padrao preenchidas e sem poder calcular`() {
        val vm = viewModel()

        assertEquals("160", vm.state.value.horas)
        assertFalse(vm.state.value.podeCalcular)
    }

    @Test
    fun `preencher valor hora valido habilita o calculo`() {
        val vm = viewModel()

        vm.aoMudarValorHora("120,00")

        assertTrue(vm.state.value.podeCalcular)
        assertNull(vm.state.value.erroValorHora)
    }

    @Test
    fun `texto invalido nas horas vira erro e bloqueia o calculo`() {
        val vm = viewModel()
        vm.aoMudarValorHora("120")

        vm.aoMudarHoras("abc")

        assertNotNull(vm.state.value.erroHoras)
        assertFalse(vm.state.value.podeCalcular)
    }

    @Test
    fun `numero negativo nas horas vira erro`() {
        val vm = viewModel()

        vm.aoMudarHoras("-10")

        assertNotNull(vm.state.value.erroHoras)
    }

    @Test
    fun `campo vazio nao mostra erro mas bloqueia o calculo`() {
        val vm = viewModel()

        vm.aoMudarHoras("")

        assertNull(vm.state.value.erroHoras)
        assertFalse(vm.state.value.podeCalcular)
    }

    @Test
    fun `calcular com entradas validas publica o resultado no estado`() {
        val vm = viewModel()
        vm.aoMudarHoras("168")
        vm.aoMudarValorHora("120")

        vm.calcular()

        val resultado = vm.state.value.resultado!!
        assertEquals(20160.00, resultado.bruto, 0.001)
        assertEquals(16632.00, resultado.liquido, 0.001)
    }

    @Test
    fun `virgula e ponto produzem o mesmo calculo`() {
        val comVirgula = viewModel().apply {
            aoMudarHoras("160")
            aoMudarValorHora("99,50")
            calcular()
        }
        val comPonto = viewModel().apply {
            aoMudarHoras("160")
            aoMudarValorHora("99.50")
            calcular()
        }

        assertEquals(
            comVirgula.state.value.resultado!!.liquido,
            comPonto.state.value.resultado!!.liquido,
            0.001
        )
    }

    @Test
    fun `o calculo guarda a base usada, para a tela de resultado`() {
        val vm = viewModel()
        vm.aoMudarHoras("168")
        vm.aoMudarValorHora("120,50")

        vm.calcular()

        assertEquals(168.0, vm.state.value.horasCalculadas, 0.001)
        assertEquals(120.50, vm.state.value.valorHoraCalculado, 0.001)
    }

    @Test
    fun `editar um campo depois do calculo limpa o resultado anterior`() {
        val vm = viewModel()
        vm.aoMudarHoras("168")
        vm.aoMudarValorHora("120")
        vm.calcular()

        vm.aoMudarHoras("100")

        assertNull(vm.state.value.resultado)
    }

    @Test
    fun `salvar mes grava o mes calculado com as aliquotas usadas`() = runTest {
        val vm = viewModel()
        vm.aoMudarHoras("168")
        vm.aoMudarValorHora("120")
        vm.calcular()

        vm.salvarMes()

        val salvo = repositorio.observarTodos().first().single()
        assertEquals(168.0, salvo.horas, 0.001)
        assertEquals(120.0, salvo.valorHora, 0.001)
        assertEquals(
            RegimeTributario.Aliquotas(inss = 11.0, iss = 5.0, irrf = 1.5),
            salvo.regime
        )
        assertEquals(vm.state.value.ano, salvo.ano)
        assertEquals(vm.state.value.mes, salvo.mes)
    }

    @Test
    fun `salvar sem ter calculado nao grava nada`() = runTest {
        val vm = viewModel()
        vm.aoMudarValorHora("120")

        vm.salvarMes()

        assertTrue(repositorio.observarTodos().first().isEmpty())
    }

    @Test
    fun `o estado sinaliza que o mes foi salvo`() = runTest {
        val vm = viewModel()
        vm.aoMudarValorHora("120")
        vm.calcular()
        assertFalse(vm.state.value.salvo)

        vm.salvarMes()

        assertTrue(vm.state.value.salvo)
    }

    @Test
    fun `recalcular depois de salvar volta a permitir salvar`() = runTest {
        val vm = viewModel()
        vm.aoMudarValorHora("120")
        vm.calcular()
        vm.salvarMes()

        vm.aoMudarHoras("100")
        vm.calcular()

        assertFalse(vm.state.value.salvo)
    }

    @Test
    fun `trocar de regime limpa o resultado anterior`() = runTest {
        val vm = viewModel()
        vm.aoMudarValorHora("120")
        vm.calcular()

        vm.aoTrocarRegime(TipoRegime.MEI)

        assertNull(vm.state.value.resultado)
    }

    @Test
    fun `no regime MEI o calculo desconta so o DAS`() = runTest {
        val vm = viewModel()
        vm.aoTrocarRegime(TipoRegime.MEI)
        vm.aoMudarValorHora("120")

        vm.calcular()

        val resultado = vm.state.value.resultado!!
        assertEquals(listOf("DAS"), resultado.descontos.map { it.nome })
        assertEquals(19119.10, resultado.liquido, 0.001)
    }

    @Test
    fun `salvar no MEI grava o regime MEI no registro`() = runTest {
        val vm = viewModel()
        vm.aoTrocarRegime(TipoRegime.MEI)
        vm.aoMudarValorHora("120")
        vm.calcular()

        vm.salvarMes()

        assertEquals(RegimeTributario.Mei(80.90), repositorio.observarTodos().first().single().regime)
    }
}
