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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pjcalc.domain.dividirCentavos
import com.pjcalc.ui.theme.JetBrainsMono
import com.pjcalc.ui.theme.PjAccent
import com.pjcalc.ui.theme.PjBackground
import com.pjcalc.ui.theme.PjBorder
import com.pjcalc.ui.theme.PjBorderStrong
import com.pjcalc.ui.theme.PjError
import com.pjcalc.ui.theme.PjSurface
import com.pjcalc.ui.theme.PjSurfaceAlt
import com.pjcalc.ui.theme.PjTextPrimary
import com.pjcalc.ui.theme.PjTextSecondary
import com.pjcalc.ui.theme.PjTextTertiary

private val PjRadius = RoundedCornerShape(16.dp)

@Composable
fun MonoLabel(
    texto: String,
    modifier: Modifier = Modifier,
    cor: androidx.compose.ui.graphics.Color = PjTextSecondary,
    maiusculas: Boolean = true
) {
    Text(
        text = if (maiusculas) texto.uppercase() else texto,
        style = MaterialTheme.typography.labelSmall,
        color = cor,
        modifier = modifier
    )
}

@Composable
fun PjCard(
    modifier: Modifier = Modifier,
    conteudo: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(PjSurface, PjRadius)
            .border(1.dp, PjBorder, PjRadius)
    ) {
        conteudo()
    }
}

@Composable
fun PjPrimaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    val fundo = if (habilitado) PjAccent else PjSurfaceAlt
    val corTexto = if (habilitado) PjBackground else PjTextTertiary
    Box(
        modifier = modifier
            .height(64.dp)
            .background(fundo, PjRadius)
            .clickable(enabled = habilitado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W700,
            color = corTexto
        )
    }
}

@Composable
fun PjSecondaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .background(PjSurfaceAlt, PjRadius)
            .border(1.dp, PjBorderStrong, PjRadius)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W700,
            color = PjTextPrimary
        )
    }
}

@Composable
fun PjNumberField(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefixo: String? = null,
    sufixo: String? = null,
    placeholder: String = "0",
    erro: String? = null
) {
    val corBorda = if (erro != null) PjError else PjBorder
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val estiloValor = MaterialTheme.typography.headlineMedium
    val medidor = rememberTextMeasurer()

    val exibido = valor.ifEmpty { placeholder }
    val corValor = if (valor.isEmpty()) PjTextTertiary else PjTextPrimary
    val larguraValor = with(LocalDensity.current) {
        medidor.measure(comCentavosMenores(exibido, estiloValor), estiloValor).size.width.toDp()
    }

    Column(modifier = modifier) {
        MonoLabel(rotulo)
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(96.dp)
                .background(PjSurface, PjRadius)
                .border(1.dp, corBorda, PjRadius)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusRequester.requestFocus() }
                )
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                if (prefixo != null) {
                    Text(
                        text = prefixo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.W600,
                        color = PjTextTertiary,
                        modifier = Modifier.padding(end = 8.dp, bottom = 6.dp)
                    )
                }
                Box(contentAlignment = Alignment.CenterStart) {
                    if (valor.isEmpty()) {
                        Text(
                            text = comCentavosMenores(placeholder, estiloValor),
                            style = estiloValor,
                            color = PjTextTertiary
                        )
                    }
                    BasicTextField(
                        value = valor,
                        onValueChange = aoMudar,
                        textStyle = estiloValor.copy(color = corValor),
                        singleLine = true,
                        cursorBrush = SolidColor(PjAccent),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        visualTransformation = { texto ->
                            TransformedText(
                                comCentavosMenores(texto.text, estiloValor),
                                OffsetMapping.Identity
                            )
                        },
                        modifier = Modifier
                            .width(larguraValor + 2.dp)
                            .focusRequester(focusRequester)
                    )
                }
                if (sufixo != null) {
                    Text(
                        text = sufixo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.W600,
                        color = PjTextTertiary,
                        modifier = Modifier.padding(start = 10.dp, bottom = 6.dp)
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

fun comCentavosMenores(texto: String, estilo: TextStyle): AnnotatedString {
    val (inteiro, centavos) = dividirCentavos(texto)
    return buildAnnotatedString {
        append(inteiro)
        if (centavos.isNotEmpty()) {
            withStyle(SpanStyle(fontSize = estilo.fontSize * 0.55f)) { append(centavos) }
        }
    }
}

@Composable
fun PjTitulo(texto: String, destaque: String, modifier: Modifier = Modifier) {
    Text(
        text = buildAnnotatedString {
            append(texto)
            withStyle(SpanStyle(color = PjAccent)) { append(destaque) }
        },
        style = MaterialTheme.typography.headlineLarge,
        color = PjTextPrimary,
        modifier = modifier
    )
}

@Composable
fun PjRodapePadrao(
    texto: String,
    acao: String,
    aoClicarAcao: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MonoLabel(texto, cor = PjTextTertiary)
        Text(
            text = acao.uppercase(),
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.W700,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            color = PjAccent,
            modifier = Modifier.clickable(onClick = aoClicarAcao)
        )
    }
}
