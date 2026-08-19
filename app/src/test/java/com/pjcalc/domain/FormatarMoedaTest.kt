package com.pjcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatarMoedaTest {
    @Test
    fun `formata milhar com ponto e centavos com virgula`() {
        assertEquals("R\$ 18.216,00", normalizar(formatarMoeda(18216.0)))
    }

    @Test
    fun `formata zero`() {
        assertEquals("R\$ 0,00", normalizar(formatarMoeda(0.0)))
    }

    @Test
    fun `formata milhao com centavos`() {
        assertEquals("R\$ 1.234.567,89", normalizar(formatarMoeda(1234567.89)))
    }

    @Test
    fun `arredonda para dois centavos`() {
        assertEquals("R\$ 2.217,60", normalizar(formatarMoeda(2217.5999999999)))
    }

    @Test
    fun `formato compacto omite os centavos`() {
        assertEquals("R\$ 18.216", normalizar(formatarMoedaCompacta(18216.0)))
        assertEquals("R\$ 15.840", normalizar(formatarMoedaCompacta(15840.49)))
    }

    private fun normalizar(valor: String) = valor.replace('\u00A0', ' ')
}
