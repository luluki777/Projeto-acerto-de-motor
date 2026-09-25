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
import com.exemplo.motortuner.core.CalculadoraCompressao
import com.exemplo.motortuner.core.CalculadoraEscape
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEscape(nav: NavController) {
    var bore by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var cilindros by remember { mutableStateOf("4") }
    var rpm by remember { mutableStateOf("") }
    var potencia by remember { mutableStateOf("") }
    var diametroAtual by remember { mutableStateOf("") }
    var esportivo by remember { mutableStateOf(false) }

    var resultado by remember { mutableStateOf<ResultadoEscape?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escape") },
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
            Campo("Potência estimada (hp)", potencia) { potencia = it }
            Campo("Diâmetro atual do escape (mm) — opcional", diametroAtual) { diametroAtual = it }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = esportivo, onCheckedChange = { esportivo = it })
                Text("Dimensionamento esportivo")
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
                val p = Numeros.parse(potencia)
                val da = Numeros.parse(diametroAtual)

                if (b == null || b <= 0) { erro = "Diâmetro deve ser maior que 0."; return@Button }
                if (s == null || s <= 0) { erro = "Curso deve ser maior que 0."; return@Button }
                if (c == null || c !in 1..16) { erro = "Número de cilindros deve estar entre 1 e 16."; return@Button }
                if (r == null || r <= 0) { erro = "RPM máximo deve ser maior que 0."; return@Button }
                if (p == null || p <= 0) { erro = "Potência estimada deve ser maior que 0."; return@Button }

                val unit = CalculadoraCompressao.volumeVarrido(b, s)
                val total = unit * c
                val diamSug = CalculadoraEscape.diametroSugeridoPorPotencia(p, esportivo)
                val (dMin, dMax) = CalculadoraEscape.faixaDiametro(diamSug)
                val velAtual = if (da != null && da > 0) {
                    CalculadoraEscape.velocidadeGases(total, r, da)
                } else null

                resultado = ResultadoEscape(
                    cilindradaTotal = total,
                    diametroSugerido = diamSug,
                    diametroMin = dMin,
                    diametroMax = dMax,
                    velocidadeAtual = velAtual,
                    areaSugerida = CalculadoraEscape.areaSecao(diamSug)
                )
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(
