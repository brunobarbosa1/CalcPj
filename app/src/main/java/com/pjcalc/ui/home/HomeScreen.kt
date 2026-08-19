package com.pjcalc.ui.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pjcalc.domain.formatarHoras
import com.pjcalc.domain.model.TipoRegime
import com.pjcalc.domain.nomeDoMes
import com.pjcalc.ui.components.MonoLabel
import com.pjcalc.ui.components.PjNumberField
import com.pjcalc.ui.components.PjSeletor
import com.pjcalc.ui.components.PjPrimaryButton
import com.pjcalc.ui.components.PjRodapePadrao
import com.pjcalc.ui.components.PjTitulo
import com.pjcalc.ui.theme.PjTextTertiary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    aoCalcular: () -> Unit,
    aoAbrirAjustes: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        MonoLabel("${nomeDoMes(state.mes)} · ${state.ano}", cor = PjTextTertiary)

        Spacer(Modifier.height(14.dp))
        PjTitulo(texto = "Quanto vou\nganhar", destaque = "?")

        Spacer(Modifier.height(28.dp))
        PjSeletor(
            opcoes = listOf("Alíquotas", "MEI"),
            selecionada = if (state.tipoRegime == TipoRegime.MEI) 1 else 0,
            aoSelecionar = { indice ->
                viewModel.aoTrocarRegime(
                    if (indice == 1) TipoRegime.MEI else TipoRegime.ALIQUOTAS
                )
            }
        )

        Spacer(Modifier.height(28.dp))
        PjNumberField(
            rotulo = "Horas no mês",
            valor = state.horas,
            aoMudar = viewModel::aoMudarHoras,
            sufixo = "h",
            erro = state.erroHoras
        )

        Spacer(Modifier.height(24.dp))
        PjNumberField(
            rotulo = "Valor / hora",
            valor = state.valorHora,
            aoMudar = viewModel::aoMudarValorHora,
            prefixo = "R$",
            placeholder = "0,00",
            erro = state.erroValorHora
        )

        Spacer(Modifier.height(36.dp))
        PjPrimaryButton(
            texto = "Calcular ganho  →",
            onClick = {
                viewModel.calcular()
                aoCalcular()
            },
            habilitado = state.podeCalcular,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))
        PjRodapePadrao(
            texto = if (state.horasPadrao > 0) {
                "${state.regime.rotulo} · Padrão: ${formatarHoras(state.horasPadrao)}h"
            } else {
                "${state.regime.rotulo} · Sem padrão"
            },
            acao = if (state.horasPadrao > 0) "Editar" else "Definir",
            aoClicarAcao = aoAbrirAjustes
        )

        Spacer(Modifier.height(24.dp))
    }
}
