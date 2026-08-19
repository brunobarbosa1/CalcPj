package com.pjcalc.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.TipoRegime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PreferenciasDataStoreTest {

    @get:Rule
    val pasta = TemporaryFolder()

    private lateinit var arquivo: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repositorio: PreferenciasRepository

    @Before
    fun criar() {
        arquivo = pasta.newFile("teste.preferences_pb")
        arquivo.delete()
        dataStore = PreferenceDataStoreFactory.create { arquivo }
        repositorio = PreferenciasDataStore(dataStore)
    }

    @After
    fun limpar() {
        arquivo.delete()
    }

    @Test
    fun sem_nada_gravado_valem_os_padroes_da_spec() = runTest {
        val prefs = repositorio.preferencias.first()

        assertEquals(TipoRegime.ALIQUOTAS, prefs.tipoRegime)
        assertEquals(160.0, prefs.horasPadrao, 0.001)
        assertEquals(RegimeTributario.Aliquotas(11.0, 5.0, 1.5), prefs.aliquotas)
    }

    @Test
    fun aliquotas_gravadas_voltam_iguais() = runTest {
        repositorio.definirAliquotas(RegimeTributario.Aliquotas(7.5, 2.0, 0.5))

        assertEquals(
            RegimeTributario.Aliquotas(7.5, 2.0, 0.5),
            repositorio.preferencias.first().aliquotas
        )
    }

    @Test
    fun o_DAS_gravado_volta_igual() = runTest {
        repositorio.definirMei(RegimeTributario.Mei(81.90))

        assertEquals(81.90, repositorio.preferencias.first().mei.das, 0.001)
    }

    @Test
    fun trocar_de_regime_preserva_a_configuracao_do_outro() = runTest {
        repositorio.definirAliquotas(RegimeTributario.Aliquotas(7.5, 2.0, 0.5))
        repositorio.definirMei(RegimeTributario.Mei(81.90))

        repositorio.definirTipoRegime(TipoRegime.MEI)

        val prefs = repositorio.preferencias.first()
        assertEquals(TipoRegime.MEI, prefs.tipoRegime)
        assertEquals(RegimeTributario.Mei(81.90), prefs.regime)
        assertEquals(RegimeTributario.Aliquotas(7.5, 2.0, 0.5), prefs.aliquotas)
    }

    @Test
    fun o_regime_ativo_acompanha_o_tipo_selecionado() = runTest {
        repositorio.definirMei(RegimeTributario.Mei(80.90))

        repositorio.definirTipoRegime(TipoRegime.ALIQUOTAS)
        assertEquals(RegimeTributario.Aliquotas(11.0, 5.0, 1.5), repositorio.preferencias.first().regime)

        repositorio.definirTipoRegime(TipoRegime.MEI)
        assertEquals(RegimeTributario.Mei(80.90), repositorio.preferencias.first().regime)
    }
}
