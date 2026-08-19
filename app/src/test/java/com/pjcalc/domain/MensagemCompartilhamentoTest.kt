package com.pjcalc.domain

import com.pjcalc.domain.model.RegimeTributario
import com.pjcalc.domain.usecase.CalcularGanhoUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MensagemCompartilhamentoTest {

    private val calcular = CalcularGanhoUseCase()

    private fun mensagem(regime: RegimeTributario, horas: Double = 168.0): String {
        val resultado = calcular(horas = horas, valorHora = 120.0, regime = regime).getOrThrow()
        return mensagemCompartilhamento(
            ano = 2026,
            mes = 8,
            horas = horas,
            valorHora = 120.0,
            resultado = resultado
        ).replace(' ', ' ')
    }

    private fun aliquotas() = RegimeTributario.Aliquotas(inss = 11.0, iss = 5.0, irrf = 1.5)

    @Test
    fun `informa a competencia e a base de calculo`() {
        val texto = mensagem(aliquotas())

        assertTrue(texto.contains("Agosto 2026"))
        assertTrue(texto.contains("168h"))
        assertTrue(texto.contains("R$ 120,00"))
    }

    @Test
    fun `informa o bruto e o liquido`() {
        val texto = mensagem(aliquotas())

        assertTrue(texto.contains("R$ 20.160,00"))
        assertTrue(texto.contains("R$ 16.632,00"))
    }

    @Test
    fun `lista cada desconto do regime por aliquotas`() {
        val texto = mensagem(aliquotas())

        assertTrue(texto.contains("INSS 11%"))
        assertTrue(texto.contains("R$ 2.217,60"))
        assertTrue(texto.contains("ISS 5%"))
        assertTrue(texto.contains("R$ 1.008,00"))
        assertTrue(texto.contains("IRRF 1,5%"))
        assertTrue(texto.contains("R$ 302,40"))
    }

    @Test
    fun `no MEI a mensagem traz o DAS e nao os impostos do outro regime`() {
        val texto = mensagem(RegimeTributario.Mei(80.90), horas = 160.0)

        assertTrue(texto.contains("DAS"))
        assertTrue(texto.contains("R$ 80,90"))
        assertTrue(texto.contains("R$ 19.119,10"))
        assertFalse(texto.contains("INSS"))
        assertFalse(texto.contains("ISS"))
        assertFalse(texto.contains("IRRF"))
    }
}
