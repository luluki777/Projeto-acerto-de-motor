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
import com.exemplo.motortuner.core.CalculadoraComando
import com.exemplo.motortuner.core.Numeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaComando(nav: NavController) {
    var durAdm by remember { mutableStateOf("") }
    var durEsc by remember { mutableStateOf("") }
    var levAdm by remember { mutableStateOf("") }
    var levEsc by remember { mutableStateOf("") }
    var lsa by remember { mutableStateOf("") }
    var relBal by remember { mutableStateOf("1,0") }

    var resultado by remember { mutableStateOf<ResultadoComando?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comando de válvulas") },
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
            Campo("Duração admissão (°)", durAdm) { durAdm = it }
            Campo("Duração escape (°)", durEsc) { durEsc = it }
            Campo("Levante admissão no came (mm)", levAdm) { levAdm = it }
            Campo("Levante escape no came (mm)", levEsc) { levEsc = it }
            Campo("LSA ( lobe separation angle, °)", lsa) { lsa = it }
            Campo("Relação dos balancins (1,0 = original)", relBal) { relBal = it }

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
                val da = Numeros.parse(durAdm)
                val de = Numeros.parse(durEsc)
                val la = Numeros.parse(levAdm)
                val le = Numeros.parse(levEsc)
                val ls = Numeros.parse(lsa)
                val rb = Numeros.parse(relBal) ?: 1.0

                if (da == null || da !in 100.0..400.0) { erro = "Duração de admissão deve estar entre 100° e 400°."; return@Button }
                if (de == null || de !in 100.0..400.0) { erro = "Duração de escape deve estar entre 100° e 400°."; return@Button }
                if (la == null || la <= 0) { erro = "Levante de admissão deve ser maior que 0."; return@Button }
                if (le == null || le <= 0) { erro = "Levante de escape deve ser maior que 0."; return@Button }
                if (ls == null || ls !in 90.0..130.0) { erro = "LSA deve estar entre 90° e 130°."; return@Button }

                val ov = CalculadoraComando.overlap(da, de, ls)
                val ev = CalculadoraComando.eventos(da, de, ls)
                val levAdmEf = CalculadoraComando.levanteEfetivo(la, rb)
                val levEscEf = CalculadoraComando.levanteEfetivo(le, rb)
                val rpmPico = CalculadoraComando.rpmPicoEstimado(da)
                val classAdm = CalculadoraComando.classificacaoDuracao(da)
                val classEsc = CalculadoraComando.classificacaoDuracao(de)

                resultado = ResultadoComando(ov, ev, levAdmEf, levEscEf, rpmPico, classAdm, classEsc, ls)
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Calcular")
            }

            resultado?.let { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Overlap", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("CALCULADO") })
                        }
                        Text("%.1f°".format(r.overlap).replace(".", ","),
                            style = MaterialTheme.typography.headlineMedium)
                        Text("Fórmula: (dur_adm + dur_esc) / 2 − 2 × LSA",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Eventos aproximados", fontWeight = FontWeight.SemiBold)
                        Text("Abertura admissão: %.1f° (positivo = antes do PMS)".format(r.eventos.aberturaAdm).replace(".", ","))
                        Text("Fechamento admissão: %.1f° após o PMI".format(r.eventos.fechamentoAdm).replace(".", ","))
                        Text("Abertura escape: %.1f° antes do PMI".format(r.eventos.aberturaEsc).replace(".", ","))
                        Text("Fechamento escape: %.1f° após o PMS".format(r.eventos.fechamentoEsc).replace(".", ","))
                        Spacer(Modifier.height(4.dp))
                        Text("Assumindo LSA simétrico. Comandos reais podem ter assimetria.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Levante efetivo", fontWeight = FontWeight.SemiBold)
                        Text("Admissão: %.2f mm".format(r.levanteAdmEf).replace(".", ","))
                        Text("Escape: %.2f mm".format(r.levanteEscEf).replace(".", ","))
                    }
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Faixa de RPM estimada", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            AssistChip(onClick = {}, label = { Text("ESTIMATIVA") })
                        }
                        Text("Pico aproximado: %d rpm".format(r.rpmPico).replace(".", ","),
                            style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text("Admissão: ${r.classAdm}")
                        Text("Escape: ${r.classEsc}")
                        Spacer(Modifier.height(4.dp))
                        Text("Fórmula empírica: RPM_pico ≈ (dur_adm − 180) × 70 + 3000",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

data class ResultadoComando(
    val overlap: Double,
    val eventos: CalculadoraComando.Eventos,
    val levanteAdmEf: Double,
    val levanteEscEf: Double,
    val rpmPico: Int,
    val classAdm: String,
    val classEsc: String,
    val lsa: Double
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
