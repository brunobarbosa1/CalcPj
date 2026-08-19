package com.pjcalc.ui.home

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pjcalc.domain.formatarHoras
import com.pjcalc.domain.formatarMoeda
import com.pjcalc.domain.mensagemCompartilhamento
import com.pjcalc.ui.components.BreakdownDescontos
import com.pjcalc.ui.components.MonoLabel
import com.pjcalc.ui.components.PjPrimaryButton
import com.pjcalc.ui.components.PjSecondaryButton
import com.pjcalc.ui.components.ValorEmDestaque
import com.pjcalc.ui.theme.PjTextTertiary

@Composable
fun ResultadoScreen(
    viewModel: HomeViewModel,
    aoVoltar: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val resultado = state.resultado ?: return
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        MonoLabel(
            texto = "←  Resultado",
            modifier = Modifier.clickable(onClick = aoVoltar)
        )

        Spacer(Modifier.height(22.dp))
        MonoLabel("Líquido a receber")

        Spacer(Modifier.height(10.dp))
        ValorEmDestaque(resultado.liquido)

        Spacer(Modifier.height(10.dp))
        MonoLabel(
            texto = "${formatarHoras(state.horasCalculadas)}h × ${formatarMoeda(state.valorHoraCalculado)}",
            cor = PjTextTertiary,
            maiusculas = false
        )

        Spacer(Modifier.height(30.dp))
        BreakdownDescontos(
            resultado = resultado,
            aliqINSS = state.aliqINSS,
            aliqISS = state.aliqISS,
            aliqIRRF = state.aliqIRRF
        )

        Spacer(Modifier.height(30.dp))
        Row {
            PjSecondaryButton(
                texto = if (state.salvo) "Salvo ✓" else "Salvar mês",
                onClick = viewModel::salvarMes,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            PjPrimaryButton(
                texto = "Compartilhar",
                onClick = {
                    val texto = mensagemCompartilhamento(
                        ano = state.ano,
                        mes = state.mes,
                        horas = state.horasCalculadas,
                        valorHora = state.valorHoraCalculado,
                        aliqINSS = state.aliqINSS,
                        aliqISS = state.aliqISS,
                        aliqIRRF = state.aliqIRRF,
                        resultado = resultado
                    )
                    val envio = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, texto)
                    }
                    context.startActivity(Intent.createChooser(envio, "Compartilhar ganho"))
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
