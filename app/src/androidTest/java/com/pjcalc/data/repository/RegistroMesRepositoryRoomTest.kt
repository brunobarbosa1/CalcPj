package com.pjcalc.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pjcalc.data.local.PjDatabase
import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.model.RegistroMes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroMesRepositoryRoomTest {
    private lateinit var db: PjDatabase
    private lateinit var repositorio: RegistroMesRepository

    @Before
    fun criarBanco() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PjDatabase::class.java).build()
        repositorio = RegistroMesRepositoryRoom(db.registroMesDao())
    }

    @After
    fun fecharBanco() = db.close()

    @Test
    fun mes_por_aliquotas_volta_do_banco_com_o_regime_intacto() = runTest {
        val regime = RegimeTributario.Aliquotas(inss = 11.0, iss = 5.0, irrf = 1.5)

        val id = repositorio.salvar(registro(mes = 8, regime = regime))

        assertEquals(regime, repositorio.porId(id)!!.regime)
    }

    @Test
    fun mes_no_MEI_volta_do_banco_com_o_DAS_intacto() = runTest {
        val regime = RegimeTributario.Mei(das = 80.90)

        val id = repositorio.salvar(registro(mes = 8, regime = regime))

        assertEquals(regime, repositorio.porId(id)!!.regime)
    }

    @Test
    fun meses_de_regimes_diferentes_convivem_na_mesma_lista() = runTest {
        repositorio.salvar(
            registro(mes = 7, regime = RegimeTributario.Aliquotas(11.0, 5.0, 1.5))
        )
        repositorio.salvar(registro(mes = 8, regime = RegimeTributario.Mei(80.90)))

        val regimes = repositorio.observarTodos().first().map { it.regime }

        assertEquals(
            listOf(
                RegimeTributario.Mei(80.90),
                RegimeTributario.Aliquotas(11.0, 5.0, 1.5)
            ),
            regimes
        )
    }

    @Test
    fun trocar_o_regime_do_mesmo_mes_substitui_o_registro() = runTest {
        repositorio.salvar(
            registro(mes = 8, regime = RegimeTributario.Aliquotas(11.0, 5.0, 1.5))
        )
        repositorio.salvar(registro(mes = 8, regime = RegimeTributario.Mei(80.90)))

        val salvos = repositorio.observarTodos().first()

        assertEquals(1, salvos.size)
        assertEquals(RegimeTributario.Mei(80.90), salvos.single().regime)
    }

    private fun registro(mes: Int, regime: RegimeTributario) = RegistroMes(
        ano = 2026,
        mes = mes,
        horas = 160.0,
        valorHora = 120.0,
        regime = regime,
        criadoEm = System.currentTimeMillis()
    )
}
