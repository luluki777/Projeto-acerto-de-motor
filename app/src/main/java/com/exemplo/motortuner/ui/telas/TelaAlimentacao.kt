package com.exemplo.motortuner.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exemplo.motortuner.core.CalculadoraAlimentacao
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAlimentacao(nav: NavController) {
    var bore by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var cilindros by remember { mutableStateOf("4") }
    var rpm by remember { mutableStateOf("") }
    var ve by remember { mutableStateOf("0,85") }
    var altaPerf by remember { mutableStateOf(false) }

    var resultado by remember { mutableStateOf<CalculadoraAlimentacao.ResultadoAlimentacao?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alimentação") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Campo("Diâmetro do pistão (mm)", bore) { bore = it }
            Campo("Curso (mm)", stroke) { stroke = it }
            Campo("Número de cilindros", cilindros) { cilindros = it }
            Campo("RPM máximo", rpm) { rpm = it }
            Campo("Eficiência volumétrica (0,80 a 1,00)", ve) { ve = it }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = altaPerf, onCheckedChange = { altaPerf = it })
                Text("Preparação de alta performance")
            }

            if (erro != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(erro!!)
                    }
                }
            }

            Button(onClick = {
                erro = null; resultado = null
                val b = Numeros.parse(bore); val s = Numeros.parse(stroke)
                val c = Numeros.parseInt(cilindros); val r = Numeros.parseInt(rpm)
                val v = Numeros.parse(ve) ?: 0.85
