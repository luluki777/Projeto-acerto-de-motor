package com.exemplo.motortuner.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exemplo.motortuner.ui.Rotas

private data class Modulo(
    val titulo: String,
    val icone: ImageVector,
    val rota: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHome(nav: NavController) {
    val modulos = listOf(
        Modulo("Motor", Icons.Filled.Build, Rotas.MOTOR),
        Modulo("Compressão", Icons.Filled.Compress, Rotas.COMPRESSAO),
        Modulo("Alimentação", Icons.Filled.LocalGasStation, Rotas.ALIMENTACAO),
        Modulo("Admissão", Icons.Filled.Air, Rotas.ADMISSAO),
        Modulo("Escape", Icons.Filled.Cloud, Rotas.ESCAPE),
        Modulo("Comando", Icons.Filled.Settings, Rotas.COMANDO),
        Modulo("Ignição", Icons.Filled.Bolt, Rotas.IGNICAO),
        Modulo("Desempenho", Icons.Filled.Speed, Rotas.DESEMPENHO),
        Modulo("Transmissão", Icons.Filled.SwapHoriz, Rotas.TRANSMISSAO),
        Modulo("Projetos", Icons.Filled.Folder, Rotas.PROJETOS),
        Modulo("Configurações", Icons.Filled.Tune, Rotas.CONFIG)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerto de Motor") }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(modulos) { m ->
                Card(
                    onClick = { nav.navigate(m.rota) },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(m.icone, contentDescription = m.titulo, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(m.titulo, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
