package com.pjcalc.ui.settings

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pjcalc.domain.model.TipoRegime
import com.pjcalc.ui.components.MonoLabel
import com.pjcalc.ui.components.PjCampoCompacto
import com.pjcalc.ui.components.PjSeletor
import com.pjcalc.ui.components.PjTitulo
import com.pjcalc.ui.theme.PjTextTertiary

@Composable
fun AjustesScreen(viewModel: AjustesViewModel = hiltViewModel()) {
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
        MonoLabel("Ajustes", cor = PjTextTertiary)
        Spacer(Modifier.height(14.dp))
        PjTitulo(texto = "Padrões", destaque = ".")

        Spacer(Modifier.height(32.dp))
        MonoLabel("Regime tributário")
        Spacer(Modifier.height(12.dp))
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
        PjCampoCompacto(
            rotulo = "Horas / mês",
            valor = state.horasPadrao,
            aoMudar = viewModel::aoMudarHorasPadrao,
            unidade = "h",
            erro = state.erroHorasPadrao
        )
        Spacer(Modifier.height(12.dp))
        PjCampoCompacto(
            rotulo = "Valor / hora",
            valor = state.valorHoraPadrao,
            aoMudar = viewModel::aoMudarValorHoraPadrao,
            prefixo = "R$",
            erro = state.erroValorHoraPadrao
        )

        Spacer(Modifier.height(28.dp))
        when (state.tipoRegime) {
            TipoRegime.ALIQUOTAS -> {
                MonoLabel("Alíquotas sobre o bruto")
                Spacer(Modifier.height(12.dp))
                PjCampoCompacto(
                    rotulo = "INSS",
                    valor = state.inss,
                    aoMudar = viewModel::aoMudarInss,
                    unidade = "%",
                    erro = state.erroInss
                )
                Spacer(Modifier.height(12.dp))
                PjCampoCompacto(
                    rotulo = "ISS",
                    valor = state.iss,
                    aoMudar = viewModel::aoMudarIss,
                    unidade = "%",
                    erro = state.erroIss
                )
                Spacer(Modifier.height(12.dp))
                PjCampoCompacto(
                    rotulo = "IRRF",
                    valor = state.irrf,
                    aoMudar = viewModel::aoMudarIrrf,
                    unidade = "%",
                    erro = state.erroIrrf
                )
            }

            TipoRegime.MEI -> {
                MonoLabel("Guia mensal")
                Spacer(Modifier.height(12.dp))
                PjCampoCompacto(
                    rotulo = "DAS",
                    valor = state.das,
                    aoMudar = viewModel::aoMudarDas,
                    prefixo = "R$",
                    erro = state.erroDas
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "O DAS é um valor fixo por mês: não muda com as horas " +
                        "trabalhadas. Consulte a guia atual no portal do MEI.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PjTextTertiary
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
