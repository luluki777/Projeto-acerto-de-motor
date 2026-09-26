package com.exemplo.motortuner.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaConfig(nav: NavController) {
    var casasDecimais by remember { mutableStateOf(2) }
    var usarVirgula by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Apresentação numérica", fontWeight = FontWeight.SemiBold)
                    Text("Casas decimais: $casasDecimais")
                    Slider(
                        value = casasDecimais.toFloat(),
                        onValueChange = { casasDecimais = it.toInt().coerceIn(0, 4) },
                        valueRange = 0f..4f,
                        steps = 3
                    )
                    Row {
                        Checkbox(checked = usarVirgula, onCheckedChange = { usarVirgula = it })
                        Text("Usar vírgula como separador decimal (padrão BR)")
                    }
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sobre", fontWeight = FontWeight.SemiBold)
                    Text("Acerto de Motor — versão 3.0")
                    Text(
                        "Ferramenta de apoio ao acerto e preparação de motores. " +
                        "Funciona offline. Todos os cálculos usam fórmulas técnicas coerentes. " +
                        "Resultados marcados como ESTIMATIVA dependem de testes e componentes reais.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Boas práticas", fontWeight = FontWeight.SemiBold)
                    Text(
                        "• Sempre confira as fórmulas no botão \"Ver cálculo\" antes de tomar decisões.\n" +
                        "• Valores estimados nunca devem ser usados como definitivos.\n" +
                        "• O ajuste final do motor depende de dinamômetro, AFR/lambda e testes reais.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
