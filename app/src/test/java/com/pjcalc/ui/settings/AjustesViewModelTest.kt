package com.pjcalc.ui.settings

import com.pjcalc.data.prefs.PreferenciasRepositoryFake
import com.pjcalc.domain.model.Preferencias
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.TipoRegime
import com.pjcalc.util.DispatcherPrincipalRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class AjustesViewModelTest {
    @get:Rule
    val dispatcherRule = DispatcherPrincipalRule()

    private val preferencias = PreferenciasRepositoryFake(
        Preferencias(
            aliquotas = RegimeTributario.Aliquotas(inss = 11.0, iss = 5.0, irrf = 1.5),
            mei = RegimeTributario.Mei(das = 80.90),
            horasPadrao = 160.0
        )
    )

    private fun viewModel() = AjustesViewModel(preferencias)

    @Test
    fun `os campos comecam com o que esta guardado`() = runTest {
        val estado = viewModel().state.value

        assertEquals("160", estado.horasPadrao)
        assertEquals("11", estado.inss)
        assertEquals("5", estado.iss)
        assertEquals("1,5", estado.irrf)
        assertEquals("80,9", estado.das)
    }

    @Test
    fun `trocar o regime persiste a escolha`() = runTest {
        viewModel().aoTrocarRegime(TipoRegime.MEI)

        assertEquals(TipoRegime.MEI, preferencias.preferencias.first().tipoRegime)
    }

    @Test
    fun `editar o DAS persiste o valor novo`() = runTest {
        viewModel().aoMudarDas("81,90")

        assertEquals(
            RegimeTributario.Mei(81.90),
            preferencias.preferencias.first().mei
        )
    }

    @Test
    fun `editar uma aliquota persiste sem mexer nas outras`() = runTest {
        viewModel().aoMudarInss("7,5")

        assertEquals(
            RegimeTributario.Aliquotas(inss = 7.5, iss = 5.0, irrf = 1.5),
            preferencias.preferencias.first().aliquotas
        )
    }

    @Test
    fun `editar as horas padrao persiste`() = runTest {
        viewModel().aoMudarHorasPadrao("176")

        assertEquals(176.0, preferencias.preferencias.first().horasPadrao, 0.001)
    }

    @Test
    fun `valor invalido vira erro e nao é gravado`() = runTest {
        val vm = viewModel()

        vm.aoMudarDas("abc")

        assertNotNull(vm.state.value.erroDas)
        assertEquals(80.90, preferencias.preferencias.first().mei.das, 0.001)
    }

    @Test
    fun `trocar de regime nao apaga a configuracao do outro`() = runTest {
        val vm = viewModel()
        vm.aoMudarDas("81,90")

        vm.aoTrocarRegime(TipoRegime.MEI)

        val guardado = preferencias.preferencias.first()
        assertEquals(RegimeTributario.Mei(81.90), guardado.mei)
        assertEquals(
            RegimeTributario.Aliquotas(inss = 11.0, iss = 5.0, irrf = 1.5),
            guardado.aliquotas
        )
    }
}
