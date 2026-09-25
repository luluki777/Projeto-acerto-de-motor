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
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaSquish(nav: NavController) {
    var espJunta by remember { mutableStateOf("") }
    var deck by remember { mutableStateOf("") }
    var pistaoAcimaDeck by remember { mutableStateOf("0") }
    var resultado by remember { mutableStateOf<Double?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Squish") },
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
            Campo("Espessura da junta (mm)", espJunta) { espJunta = it }
            Campo("Altura do deck (mm)", deck) { deck = it }
            Campo("Quanto o pistão sobe acima do deck (mm)", pistaoAcimaDeck) { pistaoAcimaDeck = it }

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
                val ej = Numeros.parse(espJunta)
                val dk = Numeros.parse(deck)
                val pa = Numeros.parse(pistaoAcimaDeck) ?: 0.0
                if (ej == null || ej <= 0) { erro = "Espessura da junta deve ser maior que 0."; return@Button }
                if (dk == null || dk < 0) { erro = "Altura do deck não pode ser negativa."; return@Button }
                resultado = ej + dk - pa
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular squish")
            }

            resultado?.let { s ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Squish", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("CALCULADO") })
                        }
                        Text("%.3f mm".format(s).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Fórmula: squish = espessura da junta + deck − pistão acima do deck")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "O valor recomendado depende do projeto específico do motor. " +
                            "Motores de alto desempenho normalmente usam squish mais apertado, " +
                            "mas isso exige controle de tolerâncias e evita colisão pistão/válvula. " +
                            "Consulte o manual ou um preparador antes de usinar.",
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
