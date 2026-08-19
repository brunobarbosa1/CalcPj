package com.pjcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatosTest {

    @Test
    fun `aliquota inteira nao mostra casa decimal`() {
        assertEquals("11%", formatarPercentual(11.0))
        assertEquals("5%", formatarPercentual(5.0))
        assertEquals("0%", formatarPercentual(0.0))
    }

    @Test
    fun `aliquota fracionada usa virgula`() {
        assertEquals("1,5%", formatarPercentual(1.5))
        assertEquals("14,25%", formatarPercentual(14.25))
    }

    @Test
    fun `horas inteiras nao mostram casa decimal`() {
        assertEquals("160", formatarHoras(160.0))
    }

    @Test
    fun `horas fracionadas usam virgula`() {
        assertEquals("7,5", formatarHoras(7.5))
    }

    @Test
    fun `mes abreviado tem tres letras em caixa alta`() {
        assertEquals("JAN", abreviacaoMes(1))
        assertEquals("MAR", abreviacaoMes(3))
        assertEquals("AGO", abreviacaoMes(8))
        assertEquals("DEZ", abreviacaoMes(12))
    }

    @Test
    fun `mes vira nome por extenso em portugues`() {
        assertEquals("Janeiro", nomeDoMes(1))
        assertEquals("Agosto", nomeDoMes(8))
        assertEquals("Dezembro", nomeDoMes(12))
    }
}
