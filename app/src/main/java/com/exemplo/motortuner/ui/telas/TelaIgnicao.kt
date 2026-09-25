package com.exemplo.motortuner.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exemplo.motortuner.core.CalculadoraIgnicao
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaIgnicao(nav: NavController) {
    var rpmInicial by remember { mutableStateOf("1000") }
    var avancoInicial by remember { mutableStateOf("10") }
    var rpmMaxAvanco by remember { mutableStateOf("3500") }
    var avancoMax by remember { mutableStateOf("32") }
    var rpmFinal by remember { mutableStateOf("7000") }
    var combustivel by remember { mutableStateOf("Gasolina") }

    var curva by remember { mutableStateOf<List<CalculadoraIgnicao.PontoCurva>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ignição") },
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
            Campo("RPM inicial", rpmInicial) { rpmInicial = it }
            Campo("Avanço inicial (°)", avancoInicial) { avancoInicial = it }
            Campo("RPM do avanço máximo", rpmMaxAvanco) { rpmMaxAvanco = it }
            Campo("Avanço máximo (°)", avancoMax) { avancoMax = it }
            Campo("RPM final da curva", rpmFinal) { rpmFinal = it }

            Text("Combustível", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Gasolina", "Etanol", "Flex").forEach { c ->
                    FilterChip(
                        selected = combustivel == c,
                        onClick = { combustivel = c },
                        label = { Text(c) }
                    )
                }
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
                erro = null
                val ri = Numeros.parseInt(rpmInicial)
                val ai = Numeros.parse(avancoInicial)
                val rm = Numeros.parseInt(rpmMaxAvanco)
                val am = Numeros.parse(avancoMax)
                val rf = Numeros.parseInt(rpmFinal)

                if (ri == null || ri < 400) { erro = "RPM inicial deve ser >= 400."; return@Button }
                if (ai == null || ai !in 0.0..45.0) { erro = "Avanço inicial deve estar entre 0° e 45°."; return@Button }
                if (rm == null || rm <= ri) { erro = "RPM de avanço máximo deve ser maior que o inicial."; return@Button }
                if (am == null || am !in 0.0..60.0) { erro = "Avanço máximo deve estar entre 0° e 60°."; return@Button }
                if (rf == null || rf <= rm) { erro = "RPM final deve ser maior que o de avanço máximo."; return@Button }

                curva = CalculadoraIgnicao.curvaBase(ri, ai, rm, am, rf)
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Gerar curva base")
            }

            if (curva.isNotEmpty()) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Curva de avanço", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth()) {
                            Text("RPM", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text("Avanço (°)", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        }
                        Divider()
                        curva.forEachIndexed { idx, ponto ->
                            var rpmTexto by remember(idx) { mutableStateOf(ponto.rpm.toString()) }
                            var avancoTexto by remember(idx) {
                                mutableStateOf("%.1f".format(ponto.avanco).replace(".", ","))
                            }
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = rpmTexto,
                                    onValueChange = { rpmTexto = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                                )
                                OutlinedTextField(
                                    value = avancoTexto,
                                    onValueChange = { avancoTexto = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "A curva base é um ponto de partida. A curva ideal depende de " +
                            "câmara de combustão, formato do pistão, octanagem, temperatura e teste em dinamômetro. " +
                            "Ajuste os pontos conforme o comportamento real do motor.",
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}
