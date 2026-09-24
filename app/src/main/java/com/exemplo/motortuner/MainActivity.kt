package com.exemplo.motortuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.PI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TelaMotor()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMotor() {
    var bore by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var rpm by remember { mutableStateOf("") }
    var camara by remember { mutableStateOf("") }
    var cilindros by remember { mutableStateOf("4") }
    var resultado by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Acerto de Motor") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = bore, onValueChange = { bore = it },
                label = { Text("Diâmetro do pistão (mm)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stroke, onValueChange = { stroke = it },
                label = { Text("Curso (mm)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cilindros, onValueChange = { cilindros = it },
                label = { Text("Número de cilindros") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = rpm, onValueChange = { rpm = it },
                label = { Text("RPM máximo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = camara, onValueChange = { camara = it },
                label = { Text("Volume da câmara (cc)") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    val b = bore.toDoubleOrNull()
                    val s = stroke.toDoubleOrNull()
                    val c = cilindros.toIntOrNull() ?: 4
                    val r = rpm.toIntOrNull()
                    val cam = camara.toDoubleOrNull()

                    if (b == null || s == null || b <= 0 || s <= 0) {
                        resultado = "Preencha diâmetro e curso corretamente"
                        return@Button
                    }

                    val ccUnit = PI / 4 * b * b * s / 1000.0
                    val ccTotal = ccUnit * c
                    val velPistao = if (r != null) 2 * s * r / 60000.0 else 0.0
                    val taxa = if (cam != null && cam > 0) (cam + ccUnit) / cam else 0.0
                    val venturi = b * 0.82

                    resultado = buildString {
                        appendLine("Cilindrada unitária: %.2f cc".format(ccUnit))
                        appendLine("Cilindrada total: %.2f cc".format(ccTotal))
                        if (r != null) appendLine("Velocidade média do pistão: %.2f m/s".format(velPistao))
                        if (taxa > 0) appendLine("Taxa de compressão: %.2f:1".format(taxa))
                        appendLine("Venturi sugerido: %.1f mm".format(venturi))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Calcular") }

            if (resultado.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(resultado, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
