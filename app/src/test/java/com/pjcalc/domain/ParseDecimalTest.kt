package com.pjcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParseDecimalTest {
    @Test
    fun `inteiro simples é aceito`() {
        assertEquals(120.0, parseDecimal("120")!!, DELTA)
    }

    @Test
    fun `virgula é aceita como separador decimal`() {
        assertEquals(120.5, parseDecimal("120,50")!!, DELTA)
    }

    @Test
    fun `ponto é aceito como separador decimal`() {
        assertEquals(120.5, parseDecimal("120.50")!!, DELTA)
    }

    @Test
    fun `zero é aceito`() {
        assertEquals(0.0, parseDecimal("0")!!, DELTA)
    }

    @Test
    fun `espacos em volta sao ignorados`() {
        assertEquals(160.0, parseDecimal("  160 ")!!, DELTA)
    }

    @Test
    fun `texto vazio é rejeitado`() {
        assertNull(parseDecimal(""))
        assertNull(parseDecimal("   "))
    }

    @Test
    fun `texto nao numerico é rejeitado`() {
        assertNull(parseDecimal("abc"))
    }

    @Test
    fun `numero negativo é rejeitado`() {
        assertNull(parseDecimal("-5"))
    }

    @Test
    fun `mais de um separador decimal é rejeitado`() {
        assertNull(parseDecimal("12,,5"))
        assertNull(parseDecimal("1,5,5"))
        assertNull(parseDecimal("1.5.5"))
    }

    private companion object {
        const val DELTA = 0.001
    }
}
