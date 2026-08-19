package com.pjcalc.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pjcalc.domain.abreviacaoMes
import com.pjcalc.domain.formatarHoras
import com.pjcalc.domain.formatarMoedaCompacta
import com.pjcalc.domain.model.RegistroCalculado
import com.pjcalc.domain.nomeDoMes
import com.pjcalc.ui.components.MonoLabel
import com.pjcalc.ui.components.PjCard
import com.pjcalc.ui.components.PjTitulo
import com.pjcalc.ui.theme.PjAccent
import com.pjcalc.ui.theme.PjBorder
import com.pjcalc.ui.theme.PjTextPrimary
import com.pjcalc.ui.theme.PjTextTertiary

private val ALTURA_GRAFICO = 132.dp
private val ALTURA_MINIMA_BARRA = 16.dp
private val LARGURA_MAXIMA_BARRA = 56.dp

@Composable
fun HistoricoScreen(
    aoAbrirDetalhe: (Long) -> Unit,
    viewModel: HistoricoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val maisRecente = state.registros.firstOrNull()?.registro?.id

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        item {
            Spacer(Modifier.height(24.dp))
            MonoLabel("Histórico", cor = PjTextTertiary)
            Spacer(Modifier.height(14.dp))
            PjTitulo(texto = "Últimos meses", destaque = "")
            Spacer(Modifier.height(32.dp))
        }

        if (state.registros.isEmpty()) {
            item { if (!state.carregando) HistoricoVazio() }
            return@LazyColumn
        }

        item {
            GraficoDeBarras(meses = state.ultimosSeis)
            Spacer(Modifier.height(32.dp))
        }

        items(state.registros, key = { it.registro.id }) { item ->
            ItemDoMes(
                item = item,
                emDestaque = item.registro.id == maisRecente,
                aoClicar = { aoAbrirDetalhe(item.registro.id) }
            )
            Spacer(Modifier.height(12.dp))
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun GraficoDeBarras(meses: List<RegistroCalculado>) {
    val maior = meses.maxOf { it.resultado.liquido }.coerceAtLeast(0.01)
    val ultimo = meses.lastOrNull()?.registro?.id

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ALTURA_GRAFICO + 28.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        meses.forEach { mes ->
            val destaque = mes.registro.id == ultimo
            val fracao = (mes.resultado.liquido / maior).coerceIn(0.0, 1.0)
            val altura = ALTURA_MINIMA_BARRA + (ALTURA_GRAFICO - ALTURA_MINIMA_BARRA) * fracao.toFloat()

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = LARGURA_MAXIMA_BARRA)
                        .fillMaxWidth()
                        .height(altura)
                        .background(
                            if (destaque) PjAccent else PjBorder,
                            RoundedCornerShape(10.dp)
                        )
                )
                Spacer(Modifier.height(10.dp))
                MonoLabel(
                    texto = abreviacaoMes(mes.registro.mes),
                    cor = if (destaque) PjAccent else PjTextTertiary
                )
            }
        }
    }
}

@Composable
private fun ItemDoMes(
    item: RegistroCalculado,
    emDestaque: Boolean,
    aoClicar: () -> Unit
) {
    PjCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = aoClicar)
                .padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = nomeDoMes(item.registro.mes),
                    style = MaterialTheme.typography.titleMedium,
                    color = PjTextPrimary
                )
                Spacer(Modifier.height(6.dp))
                MonoLabel(
                    texto = "${formatarHoras(item.registro.horas)}h · " +
                        "${formatarMoedaCompacta(item.registro.valorHora)}/h · " +
                        item.registro.regime.rotulo,
                    cor = PjTextTertiary,
                    maiusculas = false
                )
            }
            Text(
                text = formatarMoedaCompacta(item.resultado.liquido),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W700,
                color = if (emDestaque) PjAccent else PjTextPrimary
            )
        }
    }
}

@Composable
private fun HistoricoVazio() {
    Column(modifier = Modifier.padding(top = 40.dp)) {
        Text(
            text = "Nenhum mês salvo ainda.",
            style = MaterialTheme.typography.bodyLarge,
            color = PjTextTertiary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Calcule um ganho e toque em \"Salvar mês\".",
            style = MaterialTheme.typography.bodyMedium,
            color = PjTextTertiary
        )
    }
}
