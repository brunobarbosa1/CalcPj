package com.pjcalc.domain.usecase

import com.pjcalc.domain.model.RegimeTributario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalcularGanhoUseCaseTest {

    private val calcular = CalcularGanhoUseCase()

    // ---------- Regime por alíquotas ----------

    @Test
    fun `mes tipico por aliquotas produz bruto descontos e liquido`() {
        val resultado = calcular(horas = 168.0, valorHora = 120.0, regime = aliquotas()).getOrThrow()

        assertEquals(20160.00, resultado.bruto, DELTA)
        assertEquals(16632.00, resultado.liquido, DELTA)
        assertEquals(listOf("INSS", "ISS", "IRRF"), resultado.descontos.map { it.nome })
        assertEquals(listOf("11%", "5%", "1,5%"), resultado.descontos.map { it.detalhe })
        assertEquals(
            listOf(2217.60, 1008.00, 302.40),
            resultado.descontos.map { it.valor }
        )
    }

    @Test
    fun `zero horas por aliquotas zera todos os valores`() {
        val resultado = calcular(horas = 0.0, valorHora = 120.0, regime = aliquotas()).getOrThrow()

        assertEquals(0.0, resultado.bruto, DELTA)
        assertEquals(0.0, resultado.liquido, DELTA)
        assertTrue(resultado.descontos.all { it.valor == 0.0 })
    }

    @Test
    fun `sem aliquotas o liquido é igual ao bruto`() {
        val regime = RegimeTributario.Aliquotas(inss = 0.0, iss = 0.0, irrf = 0.0)

        val resultado = calcular(horas = 160.0, valorHora = 100.0, regime = regime).getOrThrow()

        assertEquals(16000.00, resultado.bruto, DELTA)
        assertEquals(16000.00, resultado.liquido, DELTA)
    }

    @Test
    fun `aliquotas somando cem por cento zeram o liquido`() {
        val regime = RegimeTributario.Aliquotas(inss = 50.0, iss = 30.0, irrf = 20.0)

        val resultado = calcular(horas = 160.0, valorHora = 100.0, regime = regime).getOrThrow()

        assertEquals(0.0, resultado.liquido, DELTA)
    }

    @Test
    fun `valores altos nao perdem precisao no centavo`() {
        val resultado =
            calcular(horas = 10_000.0, valorHora = 9_999.0, regime = aliquotas()).getOrThrow()

        assertEquals(99_990_000.00, resultado.bruto, DELTA)
        assertEquals(82_491_750.00, resultado.liquido, DELTA)
    }

    @Test
    fun `aliquota negativa é rejeitada`() {
        val regime = RegimeTributario.Aliquotas(inss = -1.0, iss = 5.0, irrf = 1.5)

        assertTrue(calcular(horas = 160.0, valorHora = 120.0, regime = regime).isFailure)
    }

    // ---------- Regime MEI ----------

    @Test
    fun `mei desconta apenas o DAS`() {
        val resultado =
            calcular(horas = 160.0, valorHora = 120.0, regime = RegimeTributario.Mei(80.90))
                .getOrThrow()

        assertEquals(19200.00, resultado.bruto, DELTA)
        assertEquals(19119.10, resultado.liquido, DELTA)
        assertEquals(listOf("DAS"), resultado.descontos.map { it.nome })
        assertEquals(80.90, resultado.descontos.single().valor, DELTA)
    }

    @Test
    fun `o DAS nao muda com as horas trabalhadas`() {
        val regime = RegimeTributario.Mei(80.90)

        val poucasHoras = calcular(horas = 40.0, valorHora = 120.0, regime = regime).getOrThrow()
        val muitasHoras = calcular(horas = 160.0, valorHora = 120.0, regime = regime).getOrThrow()

        assertEquals(
            poucasHoras.descontos.single().valor,
            muitasHoras.descontos.single().valor,
            DELTA
        )
    }

    @Test
    fun `mes que rende menos que o DAS fica com liquido negativo`() {
        val resultado =
            calcular(horas = 0.5, valorHora = 100.0, regime = RegimeTributario.Mei(80.90))
                .getOrThrow()

        assertEquals(50.00, resultado.bruto, DELTA)
        assertEquals(-30.90, resultado.liquido, DELTA)
    }

    @Test
    fun `mei sem horas ainda deve o DAS`() {
        val resultado =
            calcular(horas = 0.0, valorHora = 120.0, regime = RegimeTributario.Mei(80.90))
                .getOrThrow()

        assertEquals(0.0, resultado.bruto, DELTA)
        assertEquals(-80.90, resultado.liquido, DELTA)
    }

    @Test
    fun `DAS zerado deixa o liquido igual ao bruto`() {
        val resultado =
            calcular(horas = 160.0, valorHora = 120.0, regime = RegimeTributario.Mei(0.0))
                .getOrThrow()

        assertEquals(19200.00, resultado.liquido, DELTA)
    }

    @Test
    fun `DAS negativo é rejeitado`() {
        assertTrue(
            calcular(horas = 160.0, valorHora = 120.0, regime = RegimeTributario.Mei(-1.0))
                .isFailure
        )
    }

    // ---------- Validações comuns aos regimes ----------

    @Test
    fun `horas negativas sao rejeitadas em qualquer regime`() {
        assertTrue(calcular(horas = -1.0, valorHora = 120.0, regime = aliquotas()).isFailure)
        assertTrue(
            calcular(horas = -1.0, valorHora = 120.0, regime = RegimeTributario.Mei(80.90))
                .isFailure
        )
    }

    @Test
    fun `valor hora negativo é rejeitado em qualquer regime`() {
        assertTrue(calcular(horas = 160.0, valorHora = -120.0, regime = aliquotas()).isFailure)
        assertTrue(
            calcular(horas = 160.0, valorHora = -120.0, regime = RegimeTributario.Mei(80.90))
                .isFailure
        )
    }

    private fun aliquotas() = RegimeTributario.Aliquotas(inss = 11.0, iss = 5.0, irrf = 1.5)

    private companion object {
        const val DELTA = 0.001
    }
}
