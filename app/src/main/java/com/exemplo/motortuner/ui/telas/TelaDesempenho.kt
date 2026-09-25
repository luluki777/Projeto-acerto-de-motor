package com.exemplo.motortuner.ui.telas

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exemplo.motortuner.core.CalculadoraDesempenho
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDesempenho(nav: NavController) {
    var torque by remember { mutableStateOf("") }
    var rpmPicoTorque by remember { mutableStateOf("") }
    var rpmMax by remember { mutableStateOf("") }
    var cilindrada by remember { mutableStateOf("") }
    var potenciaHp by remember { mutableStateOf("") }
    var rpmPotencia by remember { mutableStateOf("") }

    var resultado by remember { mutableStateOf<ResultadoDesempenho?>(null) }
    var curva by remember { mutableStateOf<List<CalculadoraDesempenho.PontoDesempenho>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Desempenho") },
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
            Campo("Torque de pico (Nm)", torque) { torque = it }
            Campo("RPM do torque de pico", rpmPicoTorque) { rpmPicoTorque = it }
            Campo("RPM máximo", rpmMax) { rpmMax = it }
            Campo("Cilindrada total (cc)", cilindrada) { cilindrada = it }
            Campo("Potência (hp) — opcional", potenciaHp) { potenciaHp = it }
            Campo("RPM da potência — opcional", rpmPotencia) { rpmPotencia = it }

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
                erro = null; resultado = null; curva = emptyList()
                val t = Numeros.parse(torque)
                val rt = Numeros.parseInt(rpmPicoTorque)
                val rm = Numeros.parseInt(rpmMax)
                val cc = Numeros.parse(cilindrada)
                val ph = Numeros.parse(potenciaHp)
                val rp = Numeros.parseInt(rpmPotencia)

                if (t == null || t <= 0) { erro = "Torque de pico deve ser maior que 0."; return@Button }
                if (rt == null || rt < 800) { erro = "RPM do torque de pico deve ser >= 800."; return@Button }
                if (rm == null || rm <= rt) { erro = "RPM máximo deve ser maior que o do torque de pico."; return@Button }
                if (cc == null || cc <= 0) { erro = "Cilindrada deve ser maior que 0."; return@Button }

                val cilL = cc / 1000.0
                val bmep = CalculadoraDesempenho.bmep(t, cilL)
                val potEsp = if (ph != null && ph > 0) CalculadoraDesempenho.potenciaEspecifica(ph, cilL) else null
                val potCalc = t * rt / 7127.0
                val torqueCalc = if (ph != null && rp != null) CalculadoraDesempenho.torqueDePotencia(ph, rp) else null

                resultado = ResultadoDesempenho(bmep, potEsp, potCalc, torqueCalc, cilL)
                curva = CalculadoraDesempenho.curvaEstimada(t, rt, rm)
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular")
            }

            resultado?.let { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("BMEP", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("CALCULADO") })
                        }
                        Text("%.2f bar".format(r.bmep).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Text("Fórmula: BMEP = (Torque × 4π) / cilindrada",
                            style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Text("BMEP típico: 8-11 bar aspirado, 12-16 turbo, 18+ corrida",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Potência estimada no pico de torque", fontWeight = FontWeight.SemiBold)
                        Text("%.1f hp".format(r.potenciaNoPico).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Text("Fórmula: hp = torque_Nm × RPM / 7127",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                r.potenciaEspecifica?.let { pe ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Potência específica", fontWeight = FontWeight.SemiBold)
                            Text("%.1f hp/L".format(pe).replace(".", ","),
                                style = MaterialTheme.typography.headlineMedium)
                            Text("Aspirado comum: 60-90 hp/L. Esportivo: 100-130 hp/L.",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                if (curva.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Curvas estimadas", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                            }
                            GraficoCurvas(curva)
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Legenda("Torque (Nm)", Color(0xFFE65100))
                                Legenda("Potência (hp)", Color(0xFF1565C0))
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Estimativa baseada em formato de curva típico. Não substitui dinamômetro.",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GraficoCurvas(pontos: List<CalculadoraDesempenho.PontoDesempenho>) {
    val corTorque = Color(0xFFE65100)
    val corPot = Color(0xFF1565C0)
    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        val w = size.width
        val h = size.height
        val maxRpm = pontos.maxOf { it.rpm }.toFloat()
        val minRpm = pontos.minOf { it.rpm }.toFloat()
        val maxTorque = pontos.maxOf { it.torqueNm }.toFloat() * 1.1f
        val maxPot = pontos.maxOf { it.potenciaHp }.toFloat() * 1.1f

        // eixos
        drawLine(Color.Gray, Offset(0f, h), Offset(w, h), strokeWidth = 2f)
        drawLine(Color.Gray, Offset(0f, 0f), Offset(0f, h), strokeWidth = 2f)

        val pathT = Path()
        val pathP = Path()
        pontos.forEachIndexed { i, p ->
            val x = (p.rpm - minRpm) / (maxRpm - minRpm) * w
            val yT = h - (p.torqueNm.toFloat() / maxTorque) * h
            val yP = h - (p.potenciaHp.toFloat() / maxPot) * h
            if (i == 0) { pathT.moveTo(x, yT); pathP.moveTo(x, yP) }
            else { pathT.lineTo(x, yT); pathP.lineTo(x, yP) }
        }
        drawPath(pathT, corTorque, style = Stroke(width = 4f))
        drawPath(pathP, corPot, style = Stroke(width = 4f))
    }
}

@Composable
private fun Legenda(texto: String, cor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(12.dp)) { drawCircle(cor) }
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall)
    }
}

data class ResultadoDesempenho(
    val bmep: Double,
    val potenciaEspecifica: Double?,
    val potenciaNoPico: Double,
    val torqueCalc: Double?,
    val cilindradaL: Double
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
