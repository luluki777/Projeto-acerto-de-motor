package com.exemplo.motortuner.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exemplo.motortuner.ui.telas.*

object Rotas {
    const val HOME = "home"
    const val MOTOR = "motor"
    const val COMPRESSAO = "compressao"
    const val SQUISH = "squish"
    const val ALIMENTACAO = "alimentacao"
    const val INJECAO = "injecao"
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
        composable(Rotas.COMPRESSAO) { TelaCompressao(nav) }
        composable(Rotas.SQUISH) { TelaSquish(nav) }
        composable(Rotas.ALIMENTACAO) { TelaAlimentacao(nav) }
        composable(Rotas.INJECAO) { TelaInjecao(nav) }
        composable(Rotas.ADMISSAO) { TelaAdmissao(nav) }
        composable(Rotas.ESCAPE) { TelaEscape(nav) }
        composable(Rotas.COMANDO) { TelaPlaceholder(nav, "Comando", "Etapa 4: duração, levante, LSA, overlap.") }
        composable(Rotas.IGNICAO) { TelaPlaceholder(nav, "Ignição", "Etapa 4: curva de avanço editável.") }
        composable(Rotas.DESEMPENHO) { TelaPlaceholder(nav, "Desempenho", "Etapa 4: BMEP, potência específica, gráficos.") }
        composable(Rotas.TRANSMISSAO) { TelaPlaceholder(nav, "Transmissão", "Etapa 4: velocidade por marcha.") }
        composable(Rotas.PROJETOS) { TelaPlaceholder(nav, "Projetos", "Etapa 5: salvar, duplicar, comparar.") }
        composable(Rotas.CONFIG) { TelaPlaceholder(nav, "Configurações", "Etapa 5: casas decimais, unidades.") }
    }
}
