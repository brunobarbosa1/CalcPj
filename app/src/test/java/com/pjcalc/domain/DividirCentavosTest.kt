package com.pjcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DividirCentavosTest {
    @Test
    fun `separa os centavos de um valor com milhar`() {
        assertEquals("15.840" to ",00", dividirCentavos("15.840,00"))
    }

    @Test
    fun `separa os centavos de um valor sem milhar`() {
        assertEquals("120" to ",00", dividirCentavos("120,00"))
    }

    @Test
    fun `aceita ponto como separador decimal, do que o usuario digita`() {
        assertEquals("120" to ".50", dividirCentavos("120.50"))
    }

    @Test
    fun `valor sem centavos fica inteiro`() {
        assertEquals("168" to "", dividirCentavos("168"))
    }

    @Test
    fun `valor com varios milhares mantem só os centavos pequenos`() {
        assertEquals("1.234.567" to ",89", dividirCentavos("1.234.567,89"))
    }
}
