package com.pjcalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.pjcalc.ui.history.ARG_ID_REGISTRO
import com.pjcalc.ui.history.DetalheScreen
import com.pjcalc.ui.history.HistoricoScreen
import com.pjcalc.ui.home.HomeScreen
import com.pjcalc.ui.settings.AjustesScreen
import com.pjcalc.ui.splash.SplashScreen
import com.pjcalc.ui.home.HomeViewModel
import com.pjcalc.ui.home.ResultadoScreen
import com.pjcalc.ui.theme.JetBrainsMono
import com.pjcalc.ui.theme.PjAccent
import com.pjcalc.ui.theme.PjBackground
import com.pjcalc.ui.theme.PjBorder
import com.pjcalc.ui.theme.PjTextSecondary
import com.pjcalc.ui.theme.PjTextTertiary

private enum class Aba(val rota: String, val rotulo: String) {
    HOME(ROTA_CALCULADORA, "Home"),
    HISTORICO("historico", "Histórico"),
    AJUSTES("ajustes", "Ajustes")
}

private const val ROTA_CALCULADORA = "calculadora"
private const val ROTA_HOME = "home"
private const val ROTA_RESULTADO = "resultado"
private const val ROTA_HISTORICO_LISTA = "historico/lista"
private const val ROTA_HISTORICO_DETALHE = "historico/detalhe"

@Composable
fun PjApp(navController: NavHostController = rememberNavController()) {
    var mostrandoSplash by rememberSaveable { mutableStateOf(true) }
    if (mostrandoSplash) {
        SplashScreen(aoTerminar = { mostrandoSplash = false })
        return
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinoAtual = backStackEntry?.destination

    Scaffold(
        containerColor = PjBackground,
        bottomBar = {
            PjBottomBar(
                selecionada = { aba -> destinoAtual?.hierarchy?.any { it.route == aba.rota } == true },
                aoSelecionar = { aba -> navController.navegarParaAba(aba.rota) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Aba.HOME.rota,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation(startDestination = ROTA_HOME, route = ROTA_CALCULADORA) {
                composable(ROTA_HOME) { entry ->
                    val viewModel = viewModelDaCalculadora(navController, entry)
                    HomeScreen(
                        viewModel = viewModel,
                        aoCalcular = {
                            if (viewModel.state.value.resultado != null) {
                                navController.navigate(ROTA_RESULTADO)
                            }
                        },
                        aoAbrirAjustes = { navController.navegarParaAba(Aba.AJUSTES.rota) }
                    )
                }
                composable(ROTA_RESULTADO) { entry ->
                    ResultadoScreen(
                        viewModel = viewModelDaCalculadora(navController, entry),
                        aoVoltar = { navController.popBackStack() }
                    )
                }
            }
            navigation(startDestination = ROTA_HISTORICO_LISTA, route = Aba.HISTORICO.rota) {
                composable(ROTA_HISTORICO_LISTA) {
                    HistoricoScreen(
                        aoAbrirDetalhe = { id ->
                            navController.navigate("$ROTA_HISTORICO_DETALHE/$id")
                        }
                    )
                }
                composable(
                    route = "$ROTA_HISTORICO_DETALHE/{$ARG_ID_REGISTRO}",
                    arguments = listOf(navArgument(ARG_ID_REGISTRO) { type = NavType.LongType })
                ) {
                    DetalheScreen(aoVoltar = { navController.popBackStack() })
                }
            }
            composable(Aba.AJUSTES.rota) { AjustesScreen() }
        }
    }
}

@Composable
private fun viewModelDaCalculadora(
    navController: NavHostController,
    entry: NavBackStackEntry
): HomeViewModel {
    val entradaDoGrafo = remember(entry) { navController.getBackStackEntry(ROTA_CALCULADORA) }
    return hiltViewModel(entradaDoGrafo)
}

@Composable
private fun PjBottomBar(
    selecionada: (Aba) -> Boolean,
    aoSelecionar: (Aba) -> Unit
) {
    Column(modifier = Modifier.background(PjBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PjBorder)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Aba.entries.forEach { aba ->
                val ativa = selecionada(aba)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable { aoSelecionar(aba) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(
                                if (ativa) PjAccent else PjBackground,
                                CircleShape
                            )
                    )
                    Text(
                        text = aba.rotulo.uppercase(),
                        fontFamily = JetBrainsMono,
                        fontWeight = if (ativa) FontWeight.W700 else FontWeight.W400,
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        color = if (ativa) PjAccent else PjTextTertiary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

private fun NavHostController.navegarParaAba(rota: String) {
    navigate(rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun EmBreve(titulo: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(titulo, style = MaterialTheme.typography.labelMedium, color = PjTextSecondary)
    }
}
