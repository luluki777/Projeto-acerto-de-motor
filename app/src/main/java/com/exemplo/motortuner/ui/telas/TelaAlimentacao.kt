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
                val b = Numeros.parse(bore)
                val s = Numeros.parse(stroke)
                val c = Numeros.parseInt(cilindros)
                val r = Numeros.parseInt(rpm)
                val v = Numeros.parse(ve) ?: 0.85

                if (b == null || b <= 0) { erro = "Diâmetro deve ser maior que 0."; return@Button }
                if (s == null || s <= 0) { erro = "Curso deve ser maior que 0."; return@Button }
                if (c == null || c !in 1..16) { erro = "Número de cilindros deve estar entre 1 e 16."; return@Button }
                if (r == null || r <= 0) { erro = "RPM máximo deve ser maior que 0."; return@Button }
                if (v !in 0.5..1.2) { erro = "Eficiência volumétrica deve estar entre 0,50 e 1,20."; return@Button }

                resultado = CalculadoraAlimentacao.calcular(b, s, c, r, v, altaPerf)
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular")
            }

            resultado?.let { r ->
                // Venturi
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Venturi sugerido", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                        }
                        Text("%.1f mm".format(r.venturiSugeridoMm).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Text("Faixa: %.1f a %.1f mm".format(r.venturiMinMm, r.venturiMaxMm).replace(".", ","))
                        Spacer(Modifier.height(6.dp))
                        Text("Fórmula: √(cilindrada unitária × RPM / 1000) × k",
                            style = MaterialTheme.typography.bodySmall)
                        Text("k = 0,65 (uso geral) ou 0,80 (alta performance)",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Vazão de ar
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Vazão de ar estimada", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                        }
                        Text("%.0f m³/h".format(r.vazaoArM3H).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Text("%.1f kg/h".format(r.vazaoArKgH).replace(".", ","))
                        Spacer(Modifier.height(6.dp))
                        Text("Fórmula: cilindrada (L) × (RPM / 2) × VE × 60",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Giclê
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Faixa inicial de giclê principal", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                        }
                        r.gicleMin?.let { gMin ->
                            r.gicleMax?.let { gMax ->
                                Text("%.0f a %.0f".format(gMin, gMax),
                                    style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Card(colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )) {
                            Text(
                                "Estimativa inicial. O ajuste final deve ser realizado com AFR/lambda, " +
                                "temperatura, carga e comportamento real do motor. " +
                                "Nunca use este valor como definitivo.",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Dados usados
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Dados usados", fontWeight = FontWeight.SemiBold)
                        Text("Cilindrada unitária: %.2f cc".format(r.cilindradaUnitariaCc).replace(".", ","))
                        Text("Cilindrada total: %.2f cc".format(r.cilindradaTotalCc).replace(".", ","))
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
