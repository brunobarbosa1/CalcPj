package com.pjcalc.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroMesDaoTest {

    private lateinit var db: PjDatabase
    private lateinit var dao: RegistroMesDao

    @Before
    fun criarBanco() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PjDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.registroMesDao()
    }

    @After
    fun fecharBanco() = db.close()

    @Test
    fun lista_vem_do_mes_mais_recente_para_o_mais_antigo() = runTest {
        dao.salvar(registro(ano = 2026, mes = 6))
        dao.salvar(registro(ano = 2026, mes = 8))
        dao.salvar(registro(ano = 2025, mes = 12))
        dao.salvar(registro(ano = 2026, mes = 7))

        val meses = dao.observarTodos().first().map { it.ano to it.mes }

        assertEquals(
            listOf(2026 to 8, 2026 to 7, 2026 to 6, 2025 to 12),
            meses
        )
    }

    @Test
    fun salvar_o_mesmo_mes_de_novo_substitui_em_vez_de_duplicar() = runTest {
        dao.salvar(registro(ano = 2026, mes = 8, horas = 160.0))
        dao.salvar(registro(ano = 2026, mes = 8, horas = 168.0))

        val salvos = dao.observarTodos().first()

        assertEquals(1, salvos.size)
        assertEquals(168.0, salvos.single().horas, 0.001)
    }

    @Test
    fun meses_diferentes_convivem() = runTest {
        dao.salvar(registro(ano = 2026, mes = 7))
        dao.salvar(registro(ano = 2026, mes = 8))

        assertEquals(2, dao.observarTodos().first().size)
    }

    @Test
    fun busca_por_id_devolve_o_que_foi_salvo() = runTest {
        val id = dao.salvar(registro(ano = 2026, mes = 8, horas = 168.0, valorHora = 120.0))

        val achado = dao.porId(id)!!

        assertEquals(2026, achado.ano)
        assertEquals(8, achado.mes)
        assertEquals(168.0, achado.horas, 0.001)
        assertEquals(120.0, achado.valorHora, 0.001)
    }

    private fun registro(
        ano: Int,
        mes: Int,
        horas: Double = 160.0,
        valorHora: Double = 100.0
    ) = RegistroMesEntity(
        ano = ano,
        mes = mes,
        horas = horas,
        valorHora = valorHora,
        aliqINSS = 11.0,
        aliqISS = 5.0,
        aliqIRRF = 1.5,
        criadoEm = System.currentTimeMillis()
    )
}
