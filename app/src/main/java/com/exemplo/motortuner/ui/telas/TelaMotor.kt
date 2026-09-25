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
fun TelaMotor(nav: NavController) {
    var bore by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var cilindros by remember { mutableStateOf("4") }
    var rpm by remember { mutableStateOf("") }
    var camara by remember { mutableStateOf("") }
    var volPistao by remember { mutableStateOf("") }
    var deck by remember { mutableStateOf("") }
    var espJunta by remember { mutableStateOf("") }
    var diaJunta by remember { mutableStateOf("") }

    var detalhes by remember { mutableStateOf<List<DetalheCalculo>?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }
    var mostrarDetalhe by remember { mutableStateOf<DetalheCalculo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Motor") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoNumero("Diâmetro do pistão (mm)", bore) { bore = it }
            CampoNumero("Curso (mm)", stroke) { stroke = it }
            CampoInteiro("Número de cilindros", cilindros) { cilindros = it }
            CampoInteiro("RPM máximo", rpm) { rpm = it }
            CampoNumero("Volume da câmara (cc)", camara) { camara = it }
            CampoNumero("Volume do pistão (cc) — opcional", volPistao) { volPistao = it }
            CampoNumero("Altura do deck (mm) — opcional", deck) { deck = it }
            CampoNumero("Espessura da junta (mm) — opcional", espJunta) { espJunta = it }
            CampoNumero("Diâmetro interno da junta (mm) — opcional", diaJunta) { diaJunta = it }

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
                    erro = null
                    detalhes = null
                    val b = Numeros.parse(bore)
                    val s = Numeros.parse(stroke)
                    val c = Numeros.parseInt(cilindros)
                    val r = Numeros.parseInt(rpm)
                    val cam = Numeros.parse(camara)

                    val v = Validador.validarMotor(b, s, c, r, cam)
                    if (!v.valido) {
                        erro = v.mensagem()
                        return@Button
                    }

                    val entrada = EntradaMotor(
                        boreMm = b!!, strokeMm = s!!, cilindros = c!!,
                        rpmMax = r, volumeCamaraCc = cam,
                        volumePistaoCc = Numeros.parse(volPistao),
                        alturaDeckMm = Numeros.parse(deck),
                        espessuraJuntaMm = Numeros.parse(espJunta),
                        diametroJuntaMm = Numeros.parse(diaJunta)
                    )
                    detalhes = CalculadoraMotor.detalhar(entrada)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular")
            }

            detalhes?.forEach { d ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(d.titulo, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(
                                onClick = { mostrarDetalhe = d },
                                label = { Text(if (d.tipo == TipoResultado.CALCULADO) "CALCULADO" else "ESTIMADO") }
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(d.resultado, style = MaterialTheme.typography.headlineSmall)
                        TextButton(onClick = { mostrarDetalhe = d }) { Text("Ver cálculo") }
                    }
                }
            }
        }
    }

    if (mostrarDetalhe != null) {
        val d = mostrarDetalhe!!
        AlertDialog(
            onDismissRequest = { mostrarDetalhe = null },
            confirmButton = {
                TextButton(onClick = { mostrarDetalhe = null }) { Text("Fechar") }
            },
            title = { Text(d.titulo) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fórmula:", fontWeight = FontWeight.SemiBold)
                    Text(d.formula)
                    Spacer(Modifier.height(4.dp))
                    Text("Valores:", fontWeight = FontWeight.SemiBold)
                    Text(d.valores)
                    Spacer(Modifier.height(4.dp))
                    Text("Resultado:", fontWeight = FontWeight.SemiBold)
                    Text(d.resultado)
                }
            }
        )
    }
}

@Composable
private fun CampoNumero(rotulo: String, valor: String, onMudar: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onMudar,
        label = { Text(rotulo) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CampoInteiro(rotulo: String, valor: String, onMudar: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onMudar,
        label = { Text(rotulo) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
