package com.pjcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pjcalc.ui.theme.PjAccent
import com.pjcalc.ui.theme.PjBackground
import com.pjcalc.ui.theme.PjBorder
import com.pjcalc.ui.theme.PjError
import com.pjcalc.ui.theme.PjSurface
import com.pjcalc.ui.theme.PjTextPrimary
import com.pjcalc.ui.theme.PjTextTertiary

@Composable
fun PjCampoCompacto(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    modifier: Modifier = Modifier,
    unidade: String? = null,
    prefixo: String? = null,
    erro: String? = null
) {
    val forma = RoundedCornerShape(16.dp)
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val estilo = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp)
    val medidor = rememberTextMeasurer()
    val largura = with(LocalDensity.current) {
        medidor.measure(valor.ifEmpty { "0" }, estilo).size.width.toDp()
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(PjSurface, forma)
                .border(1.dp, if (erro != null) PjError else PjBorder, forma)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusRequester.requestFocus() }
                )
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonoLabel(rotulo)
            Row(verticalAlignment = Alignment.Bottom) {
                if (prefixo != null) {
                    Text(
                        text = prefixo,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W600,
                        color = PjTextTertiary,
                        modifier = Modifier.padding(end = 8.dp, bottom = 2.dp)
                    )
                }
                Box {
                    if (valor.isEmpty()) {
                        Text(text = "0", style = estilo, color = PjTextTertiary)
                    }
                    BasicTextField(
                        value = valor,
                        onValueChange = aoMudar,
                        textStyle = estilo.copy(color = PjTextPrimary),
                        singleLine = true,
                        cursorBrush = SolidColor(PjAccent),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .width(largura + 2.dp)
                            .focusRequester(focusRequester)
                    )
                }
                if (unidade != null) {
                    Text(
                        text = unidade,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W600,
                        color = PjTextTertiary,
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                    )
                }
            }
        }
        if (erro != null) {
            Text(
                text = erro,
                style = MaterialTheme.typography.bodyMedium,
                color = PjError,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun PjSeletor(
    opcoes: List<String>,
    selecionada: Int,
    aoSelecionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        opcoes.forEachIndexed { indice, opcao ->
            val ativa = indice == selecionada
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .background(if (ativa) PjAccent else PjSurface, forma)
                    .border(1.dp, if (ativa) PjAccent else PjBorder, forma)
                    .clickable { aoSelecionar(indice) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opcao,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W700,
                    color = if (ativa) PjBackground else PjTextPrimary
                )
            }
        }
    }
}
