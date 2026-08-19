package com.pjcalc.ui.history

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pjcalc.domain.formatarHoras
import com.pjcalc.domain.formatarMoeda
import com.pjcalc.domain.mensagemCompartilhamento
import com.pjcalc.domain.nomeDoMes
import com.pjcalc.ui.components.BreakdownDescontos
import com.pjcalc.ui.components.MonoLabel
import com.pjcalc.ui.components.PjPrimaryButton
import com.pjcalc.ui.components.ValorEmDestaque
import com.pjcalc.ui.theme.PjTextTertiary

@Composable
fun DetalheScreen(
    aoVoltar: () -> Unit,
    viewModel: DetalheViewModel = hiltViewModel()
) {
    val item by viewModel.state.collectAsStateWithLifecycle()
    val atual = item ?: return
    val registro = atual.registro
    val resultado = atual.resultado
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        MonoLabel(
            texto = "←  ${nomeDoMes(registro.mes)} ${registro.ano}",
            modifier = Modifier.clickable(onClick = aoVoltar)
        )

        Spacer(Modifier.height(22.dp))
        MonoLabel("Líquido a receber")

        Spacer(Modifier.height(10.dp))
        ValorEmDestaque(resultado.liquido)

        Spacer(Modifier.height(10.dp))
        MonoLabel(
            texto = "${formatarHoras(registro.horas)}h × ${formatarMoeda(registro.valorHora)}",
            cor = PjTextTertiary,
            maiusculas = false
        )

        Spacer(Modifier.height(30.dp))
        BreakdownDescontos(
            resultado = resultado,
            aliqINSS = registro.aliqINSS,
            aliqISS = registro.aliqISS,
            aliqIRRF = registro.aliqIRRF
        )

        Spacer(Modifier.height(30.dp))
        PjPrimaryButton(
            texto = "Compartilhar",
            onClick = {
                val texto = mensagemCompartilhamento(
                    ano = registro.ano,
                    mes = registro.mes,
                    horas = registro.horas,
                    valorHora = registro.valorHora,
                    aliqINSS = registro.aliqINSS,
                    aliqISS = registro.aliqISS,
                    aliqIRRF = registro.aliqIRRF,
                    resultado = resultado
                )
                val envio = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, texto)
                }
                context.startActivity(Intent.createChooser(envio, "Compartilhar ganho"))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
    }
}
