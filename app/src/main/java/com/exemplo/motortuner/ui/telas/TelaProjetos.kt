package com.exemplo.motortuner.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exemplo.motortuner.data.Projeto
import com.exemplo.motortuner.data.RepositorioProjetos
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaProjetos(nav: NavController) {
    val ctx = LocalContext.current
    val repo = remember { RepositorioProjetos(ctx) }
    val escopo = rememberCoroutineScope()

    val projetos by repo.listar().collectAsState(initial = emptyList())

    var mostrarDialogo by remember { mutableStateOf(false) }
    var projetoEditando by remember { mutableStateOf<Projeto?>(null) }
    var projetoComparar1 by remember { mutableStateOf<Projeto?>(null) }
    var projetoComparar2 by remember { mutableStateOf<Projeto?>(null) }
    var mostrarComparacao by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projetos") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                projetoEditando = null
                mostrarDialogo = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Novo projeto")
            }
        }
    ) { padding ->
        if (projetos.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.Folder, contentDescription = null, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Nenhum projeto salvo ainda.", fontWeight = FontWeight.SemiBold)
                Text("Toque no + para criar o primeiro.", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(projetos, key = { it.id }) { p ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(p.nome, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                AssistChip(onClick = {}, label = { Text("${p.cilindros ?: 0} cil") })
                            }
                            if (p.descricao.isNotBlank()) {
                                Text(p.descricao, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(4.dp))
                            val fmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")) }
                            Text(
                                "Alterado em ${fmt.format(Date(p.alteradoEm))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(onClick = {
                                    projetoEditando = p
                                    mostrarDialogo = true
                                }) { Text("Editar") }
                                TextButton(onClick = {
                                    escopo.launch { repo.duplicar(p) }
                                }) { Text("Duplicar") }
                                TextButton(onClick = {
                                    if (projetoComparar1 == null) projetoComparar1 = p
                                    else if (projetoComparar2 == null && projetoComparar1?.id != p.id) {
                                        projetoComparar2 = p
                                        mostrarComparacao = true
                                    } else {
                                        projetoComparar1 = p
                                        projetoComparar2 = null
                                    }
                                }) {
                                    val marcado = projetoComparar1?.id == p.id || projetoComparar2?.id == p.id
                                    Text(if (marcado) "✓ Comparar" else "Comparar")
                                }
                                TextButton(onClick = {
                                    escopo.launch { repo.excluir(p) }
                                }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoProjeto(
            projetoInicial = projetoEditando,
            onSalvar = { p ->
                escopo.launch {
                    repo.salvar(p)
                    mostrarDialogo = false
                    projetoEditando = null
                }
            },
            onCancelar = {
                mostrarDialogo = false
                projetoEditando = null
            }
        )
    }

    if (mostrarComparacao && projetoComparar1 != null && projetoComparar2 != null) {
        DialogoComparacao(
            p1 = projetoComparar1!!,
            p2 = projetoComparar2!!,
            onFechar = {
                mostrarComparacao = false
                projetoComparar1 = null
                projetoComparar2 = null
            }
        )
    }
}

@Composable
private fun DialogoProjeto(
    projetoInicial: Projeto?,
    onSalvar: (Projeto) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf(projetoInicial?.nome ?: "") }
    var descricao by remember { mutableStateOf(projetoInicial?.descricao ?: "") }
    var bore by remember { mutableStateOf(projetoInicial?.boreMm?.toString() ?: "") }
    var stroke by remember { mutableStateOf(projetoInicial?.strokeMm?.toString() ?: "") }
    var cilindros by remember { mutableStateOf(projetoInicial?.cilindros?.toString() ?: "") }
    var rpmMax by remember { mutableStateOf(projetoInicial?.rpmMax?.toString() ?: "") }
    var camara by remember { mutableStateOf(projetoInicial?.volumeCamaraCc?.toString() ?: "") }
    var taxa by remember { mutableStateOf(projetoInicial?.taxaCompressao?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                enabled = nome.isNotBlank(),
                onClick = {
                    val base = projetoInicial ?: Projeto(nome = nome)
                    onSalvar(
                        base.copy(
                            nome = nome,
                            descricao = descricao,
                            boreMm = bore.toDoubleOrNull(),
                            strokeMm = stroke.toDoubleOrNull(),
                            cilindros = cilindros.toIntOrNull(),
                            rpmMax = rpmMax.toIntOrNull(),
                            volumeCamaraCc = camara.toDoubleOrNull(),
                            taxaCompressao = taxa.toDoubleOrNull()
                        )
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
        title = { Text(if (projetoInicial == null) "Novo projeto" else "Editar projeto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoDialogo("Nome do projeto *", nome) { nome = it }
                CampoDialogo("Descrição", descricao) { descricao = it }
                CampoDialogo("Diâmetro (mm)", bore) { bore = it }
                CampoDialogo("Curso (mm)", stroke) { stroke = it }
                CampoDialogo("Cilindros", cilindros) { cilindros = it }
                CampoDialogo("RPM máx", rpmMax) { rpmMax = it }
                CampoDialogo("Câmara (cc)", camara) { camara = it }
                CampoDialogo("Taxa de compressão", taxa) { taxa = it }
            }
        }
    )
}

@Composable
private fun CampoDialogo(rotulo: String, valor: String, onMudar: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onMudar,
        label = { Text(rotulo) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DialogoComparacao(p1: Projeto, p2: Projeto, onFechar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onFechar,
        confirmButton = { TextButton(onClick = onFechar) { Text("Fechar") } },
        title = { Text("Comparar projetos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Text("Campo", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(p1.nome, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(p2.nome, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                }
                Divider()
                LinhaComparacao("Cilindros", p1.cilindros?.toString(), p2.cilindros?.toString())
                LinhaComparacao("Diâmetro", p1.boreMm?.toString(), p2.boreMm?.toString())
                LinhaComparacao("Curso", p1.strokeMm?.toString(), p2.strokeMm?.toString())
                LinhaComparacao("Câmara", p1.volumeCamaraCc?.toString(), p2.volumeCamaraCc?.toString())
                LinhaComparacao(
                    "Taxa",
                    p1.taxaCompressao?.let { "%.2f".format(it) },
                    p2.taxaCompressao?.let { "%.2f".format(it) }
                )
                LinhaComparacao("RPM máx", p1.rpmMax?.toString(), p2.rpmMax?.toString())
            }
        }
    )
}

@Composable
private fun LinhaComparacao(rotulo: String, v1: String?, v2: String?) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(rotulo, modifier = Modifier.weight(1f))
        Text(v1 ?: "—", modifier = Modifier.weight(1f))
        Text(v2 ?: "—", modifier = Modifier.weight(1f))
    }
}
