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
import com.exemplo.motortuner.core.CalculadoraTransmissao
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaTransmissao(nav: NavController) {
    var aro by remember { mutableStateOf("15") }
    var largura by remember { mutableStateOf("195") }
    var perfil by remember { mutableStateOf("55") }
    var relDiferencial by remember { mutableStateOf("") }
    var rpm by remember { mutableStateOf("") }
    var rel1 by remember { mutableStateOf("") }
    var rel2 by remember { mutableStateOf("") }
    var rel3 by remember { mutableStateOf("") }
    var rel4 by remember { mutableStateOf("") }
    var rel5 by remember { mutableStateOf("") }

    var resultado by remember { mutableStateOf<ResultadoTransmissao?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transmissão") },
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
            Text("Pneu", fontWeight = FontWeight.SemiBold)
            Campo("Aro (polegadas)", aro) { aro = it }
            Campo("Largura (mm)", largura) { largura = it }
            Campo("Perfil (%)", perfil) { perfil = it }

            Spacer(Modifier.height(4.dp))
            Text("Transmissão", fontWeight = FontWeight.SemiBold)
            Campo("Relação do diferencial", relDiferencial) { relDiferencial = it }
            Campo("RPM para o cálculo", rpm) { rpm = it }

            Text("Relações de marcha (deixe vazio o que não usar)", fontWeight = FontWeight.SemiBold)
            Campo("1ª marcha", rel1) { rel1 = it }
            Campo("2ª marcha", rel2) { rel2 = it }
            Campo("3ª marcha", rel3) { rel3 = it }
            Campo("4ª marcha", rel4) { rel4 = it }
            Campo("5ª marcha", rel5) { rel5 = it }

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
                val a = Numeros.parse(aro)
                val l = Numeros.parse(largura)
                val pf = Numeros.parse(perfil)
                val rd = Numeros.parse(relDiferencial)
                val r = Numeros.parseInt(rpm)
                val rels = listOf(rel1, rel2, rel3, rel4, rel5)
                    .map { Numeros.parse(it) }
                    .filterNotNull()

                if (a == null || a <= 0) { erro = "Aro deve ser maior que 0."; return@Button }
                if (l == null || l <= 0) { erro = "Largura deve ser maior que 0."; return@Button }
                if (pf == null || pf <= 0) { erro = "Perfil deve ser maior que 0."; return@Button }
                if (rd == null || rd <= 0) { erro = "Relação do diferencial deve ser maior que 0."; return@Button }
                if (r == null || r <= 0) { erro = "RPM deve ser maior que 0."; return@Button }
                if (rels.isEmpty()) { erro = "Informe ao menos uma relação de marcha."; return@Button }

                val circ = CalculadoraTransmissao.circunferenciaPneu(a, l, pf)
                val linhas = CalculadoraTransmissao.tabelaPorMarcha(r, circ, rd, rels)
                resultado = ResultadoTransmissao(circ, linhas)
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular")
            }

            resultado?.let { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Circunferência do pneu", fontWeight = FontWeight.SemiBold)
                        Text("%.3f m".format(r.circunferencia).replace(".", ","),
                            style = MaterialTheme.typography.titleLarge)
                        Text("Fórmula: π × (aro + 2 × largura × perfil/100) / 1000",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Velocidade por marcha", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("CALCULADO") })
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.fillMaxWidth()) {
                            Text("Marcha", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text("Relação", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text("RPM", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text("km/h", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        }
                        Divider()
                        r.linhas.forEach { linha ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Text("${linha.marcha}ª", modifier = Modifier.weight(1f))
                                Text("%.2f".format(linha.relacao).replace(".", ","), modifier = Modifier.weight(1f))
                                Text("%d".format(linha.rpm), modifier = Modifier.weight(1f))
                                Text("%.1f".format(linha.velocidadeKmh).replace(".", ","), modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Aviso", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Cálculo teórico — não considera escorregamento do conversor, " +
                            "deformação do pneu sob carga, perdas aerodinâmicas ou limite de potência. " +
                            "Use como referência.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

data class ResultadoTransmissao(
    val circunferencia: Double,
    val linhas: List<CalculadoraTransmissao.LinhaMarcha>
)

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
