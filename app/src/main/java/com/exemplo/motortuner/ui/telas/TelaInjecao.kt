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
import com.exemplo.motortuner.core.CalculadoraInjecao
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaInjecao(nav: NavController) {
    var potencia by remember { mutableStateOf("") }
    var cilindros by remember { mutableStateOf("4") }
    var bsfc by remember { mutableStateOf("") }
    var duty by remember { mutableStateOf("0,80") }
    var pressao by remember { mutableStateOf("3,0") }

    var resultado by remember { mutableStateOf<CalculadoraInjecao.ResultadoInjecao?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Injeção eletrônica") },
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
            Campo("Potência desejada (hp)", potencia) { potencia = it }
            Campo("Número de cilindros", cilindros) { cilindros = it }
            Campo("BSFC lb/(hp·h) — vazio = sugerido", bsfc) { bsfc = it }
            Campo("Duty cycle máximo (0,80 = 80%)", duty) { duty = it }
            Campo("Pressão de combustível (bar)", pressao) { pressao = it }

            Text(
                "BSFC sugerido: aspirado gasolina 0,48 / turbo gasolina 0,60 / " +
                "etanol 0,60 a 0,70. Deixe vazio para o app usar o sugerido (0,48).",
                style = MaterialTheme.typography.bodySmall
            )

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
                val p = Numeros.parse(potencia)
                val c = Numeros.parseInt(cilindros)
                val d = Numeros.parse(duty)
                val b = Numeros.parse(bsfc) ?: 0.48

                if (p == null || p <= 0) { erro = "Potência deve ser maior que 0."; return@Button }
                if (c == null || c !in 1..16) { erro = "Número de cilindros deve estar entre 1 e 16."; return@Button }
                if (d == null || d !in 0.1..1.0) { erro = "Duty cycle deve estar entre 0,10 e 1,00."; return@Button }
                if (b <= 0) { erro = "BSFC deve ser maior que 0."; return@Button }

                resultado = CalculadoraInjecao.calcular(p, c, b, d)
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular")
            }

            resultado?.let { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Vazão por bico", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("CALCULADO") })
                        }
                        Text("%.1f cc/min".format(r.vazaoPorBicoCcMin).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Text("%.2f lb/h".format(r.vazaoPorBicoLbH).replace(".", ","))
                        Spacer(Modifier.height(8.dp))
                        Text("Vazão total: %.0f cc/min (%.1f lb/h)"
                            .format(r.vazaoTotalCcMin, r.vazaoTotalLbH).replace(".", ","))
                        Spacer(Modifier.height(8.dp))
                        Text("Como foi calculado", fontWeight = FontWeight.SemiBold)
                        Text("Q_total = (Potência × BSFC) / duty cycle",
                            style = MaterialTheme.typography.bodySmall)
                        Text("Q_bico = Q_total / nº de bicos",
                            style = MaterialTheme.typography.bodySmall)
                        Text("BSFC usado: %.2f lb/(hp·h)".format(r.bsfcUsado).replace(".", ","))
                        Text("Duty cycle usado: %.0f%%".format(r.dutyCycleUsado * 100))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "O resultado real depende do combustível, pressão de linha, " +
                            "temperatura do ar e estratégia do ECU. Use este valor como ponto de partida.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Campo(rotulo: String, valor: String, onMudar: (String) -> Unit) {
    OutlinedTextField(
        value = valor, onValueChange = onMudar, label = { Text(rotulo) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
