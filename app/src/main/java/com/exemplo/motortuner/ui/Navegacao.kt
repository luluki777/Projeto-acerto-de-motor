package com.exemplo.motortuner.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exemplo.motortuner.ui.telas.TelaHome
import com.exemplo.motortuner.ui.telas.TelaMotor
import com.exemplo.motortuner.ui.telas.TelaPlaceholder

object Rotas {
    const val HOME = "home"
    const val MOTOR = "motor"
    const val COMPRESSAO = "compressao"
    const val ALIMENTACAO = "alimentacao"
    const val ADMISSAO = "admissao"
    const val ESCAPE = "escape"
    const val COMANDO = "comando"
    const val IGNICAO = "ignicao"
    const val DESEMPENHO = "desempenho"
    const val TRANSMISSAO = "transmissao"
    const val PROJETOS = "projetos"
    const val CONFIG = "config"
}

@Composable
fun AppMotorTuner() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Rotas.HOME) {
        composable(Rotas.HOME) { TelaHome(nav) }
        composable(Rotas.MOTOR) { TelaMotor(nav) }
        composable(Rotas.COMPRESSAO) { TelaPlaceholder(nav, "Compressão", "Em breve: cálculo de taxa de compressão com simulador de junta.") }
        composable(Rotas.ALIMENTACAO) { TelaPlaceholder(nav, "Alimentação", "Em breve: dimensionamento de carburador e injeção.") }
        composable(Rotas.ADMISSAO) { TelaPlaceholder(nav, "Admissão", "Em breve: duto, coletor e velocidade de fluxo.") }
        composable(Rotas.ESCAPE) { TelaPlaceholder(nav, "Escape", "Em breve: dimensionamento de escape.") }
        composable(Rotas.COMANDO) { TelaPlaceholder(nav, "Comando", "Em breve: duração, levante, LSA, overlap.") }
        composable(Rotas.IGNICAO) { TelaPlaceholder(nav, "Ignição", "Em breve: curva de avanço editável.") }
        composable(Rotas.DESEMPENHO) { TelaPlaceholder(nav, "Desempenho", "Em breve: BMEP, potência específica, gráficos.") }
        composable(Rotas.TRANSMISSAO) { TelaPlaceholder(nav, "Transmissão", "Em breve: velocidade por marcha.") }
        composable(Rotas.PROJETOS) { TelaPlaceholder(nav, "Projetos", "Em breve: salvar, duplicar, comparar projetos.") }
        composable(Rotas.CONFIG) { TelaPlaceholder(nav, "Configurações", "Em breve: casas decimais, unidades.") }
    }
}
