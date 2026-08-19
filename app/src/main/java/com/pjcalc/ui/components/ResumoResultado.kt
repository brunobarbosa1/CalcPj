package com.pjcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjcalc.domain.formatarMoeda
import com.pjcalc.domain.formatarValor
import com.pjcalc.domain.model.Desconto
import com.pjcalc.domain.model.ResultadoCalculo
import com.pjcalc.ui.theme.PjAccent
import com.pjcalc.ui.theme.PjBorder
import com.pjcalc.ui.theme.PjError
import com.pjcalc.ui.theme.PjTextPrimary
import com.pjcalc.ui.theme.PjTextSecondary
import com.pjcalc.ui.theme.PjTextTertiary

@Composable
fun ValorEmDestaque(liquido: Double, modifier: Modifier = Modifier) {
    val estilo = MaterialTheme.typography.displayLarge
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = "R$",
            style = MaterialTheme.typography.titleMedium,
            color = PjTextPrimary,
            modifier = Modifier.padding(end = 10.dp, bottom = 10.dp)
        )
        Text(
            text = comCentavosMenores(formatarValor(liquido), estilo),
            style = estilo,
            color = PjAccent
        )
    }
}

@Composable
fun BreakdownDescontos(
    resultado: ResultadoCalculo,
    modifier: Modifier = Modifier
) {
    PjCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bruto",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PjTextPrimary
                )
                Text(
                    text = formatarMoeda(resultado.bruto),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W700,
                    color = PjTextPrimary
                )
            }

            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PjBorder)
            )
            Spacer(Modifier.height(20.dp))

            resultado.descontos.forEachIndexed { indice, desconto ->
                if (indice > 0) Spacer(Modifier.height(16.dp))
                LinhaDesconto(desconto)
            }
        }
    }
}

@Composable
private fun LinhaDesconto(desconto: Desconto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = desconto.nome,
                style = MaterialTheme.typography.bodyLarge,
                color = PjTextSecondary
            )
            if (desconto.detalhe != null) {
                Text(
                    text = desconto.detalhe,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PjTextTertiary,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
        Text(
            text = "−  ${formatarMoeda(desconto.valor)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W600,
            color = PjError
        )
    }
}
