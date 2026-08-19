package com.pjcalc.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pjcalc.ui.theme.PjAccent
import com.pjcalc.ui.theme.PjBackground
import kotlinx.coroutines.delay

private const val DURACAO_MS = 900L

@Composable
fun SplashScreen(aoTerminar: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(DURACAO_MS)
        aoTerminar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PjBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "PJ.",
            style = MaterialTheme.typography.displayLarge,
            color = PjAccent
        )
    }
}
