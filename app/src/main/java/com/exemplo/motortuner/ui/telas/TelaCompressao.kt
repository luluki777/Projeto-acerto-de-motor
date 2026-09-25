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
import com.exemplo.motortuner.core.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCompressao(nav: NavController) {
    var bore by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var camara by remember { mutableStateOf("") }
    var diaJunta by remember { mutableStateOf("") }
    var espJunta by remember { mutableStateOf("") }
    var deck by remember { mutableStateOf("") }
    var volPistao by remember { mutableStateOf("0") }

    var resultado by remember { mutableStateOf<ResultadoCompressao?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    var novaEspJunta by remember { mutableStateOf("") }
    var novaCamara by remember { mutableStateOf("") }
    var simJunta by remember { mutableStateOf<Double?>(null) }
    var simCamara by remember { mutableStateOf<Double?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compressão") },
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
            Campo("Volume da câmara (cc)", camara) { camara = it }
            Campo("Diâmetro interno da junta (mm)", diaJunta) { diaJunta = it }
            Campo("Espessura da junta (mm)", espJunta) { espJunta = it }
            Campo("Altura do deck (mm)", deck) { deck = it }
            Campo("Volume do pistão (cc) — use 0 se plano", volPistao) { volPistao = it }

            if (erro != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(erro!!)
                    }
                }
            }

            Button(
                onClick = {
                    erro = null; resultado = null; simJunta = null; simCamara = null
                    val b = Numeros.parse(bore); val s = Numeros.parse(stroke)
                    val cam = Numeros.parse(camara); val dj = Numeros.parse(diaJunta)
                    val ej = Numeros.parse(espJunta); val dk = Numeros.parse(deck)
                    val vp = Numeros.parse(volPistao) ?: 0.0

                    if (b == null || b <= 0) { erro = "Diâmetro deve ser maior que 0."; return@Button }
                    if (s == null || s <= 0) { erro = "Curso deve ser maior que 0."; return@Button }
                    if (cam == null || cam <= 0) { erro = "Volume da câmara deve ser maior que 0."; return@Button }
                    if (dj == null || dj <= 0) { erro = "Diâmetro da junta deve ser maior que 0."; return@Button }
                    if (ej == null || ej <= 0) { erro = "Espessura da junta deve ser maior que 0."; return@Button }
                    if (dk == null || dk < 0) { erro = "Altura do deck não pode ser negativa."; return@Button }

                    val vd = CalculadoraCompressao.volumeVarrido(b, s)
                    val vj = CalculadoraCompressao.volumeJunta(dj, ej)
                    val vdeck = CalculadoraCompressao.volumeDeck(b, dk)
                    val vc = CalculadoraCompressao.volumeResidual(cam, vj, vdeck, vp)
                    val cr = CalculadoraCompressao.taxaCompressao(vd, vc)

                    resultado = ResultadoCompressao(vd, vj, vdeck, vc, cr, cam)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular taxa")
            }

            resultado?.let { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Taxa de compressão", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("CALCULADO") })
                        }
                        Text("%.2f:1".format(r.taxa).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Volume varrido: %.2f cc".format(r.volumeVarrido).replace(".", ","))
                        Text("Volume da junta: %.3f cc".format(r.volumeJunta).replace(".", ","))
                        Text("Volume do deck: %.3f cc".format(r.volumeDeck).replace(".", ","))
                        Text("Volume residual: %.3f cc".format(r.volumeResidual).replace(".", ","))

                        Divider(Modifier.padding(vertical = 8.dp))
                        Text("Como foi calculado", fontWeight = FontWeight.SemiBold)
                        Text("CR = (Vd + Vc) / Vc")
                        Text("Vd = π/4 × D² × curso / 1000 = %.2f cc".format(r.volumeVarrido).replace(".", ","))
                        Text("Vc = câmara + junta + deck + pistão = %.3f cc".format(r.volumeResidual).replace(".", ","))
                    }
                }

                // Simulador de junta
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Simular troca de junta", fontWeight = FontWeight.SemiBold)
                        Campo("Nova espessura da junta (mm)", novaEspJunta) { novaEspJunta = it }
                        Button(onClick = {
                            val nova = Numeros.parse(novaEspJunta)
                            val dj = Numeros.parse(diaJunta)
                            if (nova != null && nova > 0 && dj != null) {
                                simJunta = CalculadoraCompressao.simularJunta(
                                    r.volumeVarrido, r.camara, r.volumeDeck,
                                    r.volumeResidual - r.camara - r.volumeJunta - r.volumeDeck,
                                    dj, nova
                                )
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text("Simular") }
                        simJunta?.let {
                            Text("Nova taxa: %.2f:1".format(it).replace(".", ","),
                                style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }

                // Simulador de câmara
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Simular alteração da câmara", fontWeight = FontWeight.SemiBold)
                        Campo("Novo volume da câmara (cc)", novaCamara) { novaCamara = it }
                        Button(onClick = {
                            val nova = Numeros.parse(novaCamara)
                            if (nova != null && nova > 0) {
                                simCamara = CalculadoraCompressao.simularCamara(
                                    r.volumeVarrido, r.volumeJunta, r.volumeDeck,
                                    r.volumeResidual - r.camara - r.volumeJunta - r.volumeDeck,
                                    nova
                                )
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text("Simular") }
                        simCamara?.let {
                            Text("Nova taxa: %.2f:1".format(it).replace(".", ","),
                                style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}

data class ResultadoCompressao(
    val volumeVarrido: Double,
    val volumeJunta: Double,
    val volumeDeck: Double,
    val volumeResidual: Double,
    val taxa: Double,
    val camara: Double
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
