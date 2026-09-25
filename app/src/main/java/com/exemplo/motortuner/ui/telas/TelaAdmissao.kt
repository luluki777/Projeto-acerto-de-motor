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
import com.exemplo.motortuner.core.CalculadoraAdmissao
import com.exemplo.motortuner.core.CalculadoraCompressao
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdmissao(nav: NavController) {
    var bore by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var cilindros by remember { mutableStateOf("4") }
    var rpm by remember { mutableStateOf("") }
    var diametroDuto by remember { mutableStateOf("") }
    var velAlvo by remember { mutableStateOf("80") }
    var rpmAlvoColetor by remember { mutableStateOf("") }

    var resultado by remember { mutableStateOf<ResultadoAdmissao?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admissão") },
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
            Campo("Diâmetro do duto atual (mm) — opcional", diametroDuto) { diametroDuto = it }
            Campo("Velocidade-alvo (m/s) — 60 a 90 comum, 90 a 120 esportivo", velAlvo) { velAlvo = it }
            Campo("RPM alvo do coletor — opcional", rpmAlvoColetor) { rpmAlvoColetor = it }

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
                val dd = Numeros.parse(diametroDuto)
                val va = Numeros.parse(velAlvo) ?: 80.0
                val ra = Numeros.parseInt(rpmAlvoColetor)

                if (b == null || b <= 0) { erro = "Diâmetro deve ser maior que 0."; return@Button }
                if (s == null || s <= 0) { erro = "Curso deve ser maior que 0."; return@Button }
                if (c == null || c !in 1..16) { erro = "Número de cilindros deve estar entre 1 e 16."; return@Button }
                if (r == null || r <= 0) { erro = "RPM máximo deve ser maior que 0."; return@Button }
                if (va <= 0) { erro = "Velocidade-alvo deve ser maior que 0."; return@Button }

                val unit = CalculadoraCompressao.volumeVarrido(b, s)
                val diamSug = CalculadoraAdmissao.diametroSugerido(unit, r, va)
                val areaSug = CalculadoraAdmissao.areaSecao(diamSug)
                val velAtual = if (dd != null && dd > 0) {
                    CalculadoraAdmissao.velocidadeFluxo(unit, r, dd)
                } else null
                val compColetor = if (ra != null && ra > 0) {
                    CalculadoraAdmissao.comprimentoColetor(ra)
                } else null

                resultado = ResultadoAdmissao(
                    cilindradaUnitaria = unit,
                    diametroSugerido = diamSug,
                    areaSugerida = areaSug,
                    velocidadeAtual = velAtual,
                    comprimentoColetor = compColetor,
                    velocidadeAlvo = va
                )
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular")
            }

            resultado?.let { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Diâmetro de duto sugerido", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                        }
                        Text("%.1f mm".format(r.diametroSugerido).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Text("Área da seção: %.0f mm²".format(r.areaSugerida))
                        Text("Para atingir %.0f m/s".format(r.velocidadeAlvo))
                        Spacer(Modifier.height(6.dp))
                        Text("Fórmula: D = √(4 × Q / (π × v))",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                r.velocidadeAtual?.let { va ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Velocidade no duto atual", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                AssistChip(onClick = {}, label = { Text("CALCULADO") })
                            }
                            Text("%.1f m/s".format(va).replace(".", ","),
                                style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(6.dp))
                            Text("Faixa comum: 60 a 90 m/s (aspirado), 90 a 120 m/s (esportivo)",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                r.comprimentoColetor?.let { cc ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Comprimento de coletor (3º harmônico)", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                            }
                            Text("%.0f mm".format(cc).replace(".", ","),
                                style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(6.dp))
                            Text("Fórmula de ressonância: L = (Cs / f) × n / 4, Cs = 343 m/s",
                                style = MaterialTheme.typography.bodySmall)
                            Text("Depende também do comando, cabeçote e aplicação.",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Cilindrada unitária usada: %.2f cc"
                            .format(r.cilindradaUnitaria).replace(".", ","))
                    }
                }
            }
        }
    }
}

data class ResultadoAdmissao(
    val cilindradaUnitaria: Double,
    val diametroSugerido: Double,
    val areaSugerida: Double,
    val velocidadeAtual: Double?,
    val comprimentoColetor: Double?,
    val velocidadeAlvo: Double
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
